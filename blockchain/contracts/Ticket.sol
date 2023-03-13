// SPDX-License-Identifier: MIT
pragma solidity >=0.8.0 <0.9.0;
import "@openzeppelin/contracts/token/ERC721/extensions/ERC721Enumerable.sol";
import "@openzeppelin/contracts/utils/Counters.sol";
import "./TicketDTO.sol";
/*
* 공연 티켓을 ERC-721 Token(NFT)으로 생성하는 Contract
* @author 상미니
*/

contract Ticket is ERC721Enumerable, TicketDTO {
    using Counters for Counters.Counter;
    constructor() ERC721("TTOKET", "TKT") {}


    // NFT(티켓) 생성 시 마다 1씩 증가하는 ID
    Counters.Counter private _tokenIds;

    // NFT 메타데이터 URI 저장을 위한 mapping
    // TokenId => IPFS해시주소
    mapping(uint256 => string) private _tokenURIs;

    // TokenId => 티켓정보
    mapping(uint256 => TicketInfo) private _ticketInfos;

    // NFT 토큰 발행 지갑의 Address 저장을 위한 mapping
    // TokenId => 주인
    mapping(uint256 => address) private _minters;

    // address => TokenId[]
    mapping(address => uint256[]) private _ticketsByAccount;

    // performId => 공연정보
    mapping(uint256 => PerformInfo) private _performInfos;

    // performId => 비하인드 IPFS해시주소 배열
    mapping(uint256 => string[]) private _performBehinds;

    function createPerform(uint256 performId, address organizer, string memory title,
                        string memory description, uint256 maxSeat, string memory location,
                        uint256 price, string memory performDate, string memory poster) public returns (uint256){
        PerformInfo memory p = PerformInfo(performId,organizer,title,description,maxSeat,location,price,performDate,poster);
        _setPerformInfo(performId,p);

        return performId;
    }
    /*
    * create
    * 새로운 티켓 정보를 가진 ERC-721 토큰을 생성
    * 
    * @ param 
    * @ return None
    * @ exception None
    */
    function createTicket(string memory ticketURI, uint256 performId, 
                    string memory performName, string memory location, 
                    string memory userName, uint256 seatNum) public payable returns (uint256) {
        _tokenIds.increment();

        uint256 newTokenId = _tokenIds.current();
        TicketInfo memory t = TicketInfo(ticketURI,performId,performName,location,userName,seatNum,0);
        _mint(msg.sender, newTokenId); 
        _setMinter(newTokenId, msg.sender);
        _setTokenURI(newTokenId, ticketURI);
        _setTicketInfo(newTokenId, t);
        _setTicketsByAccount(newTokenId, msg.sender);
        return newTokenId;
    }
    function insertPerformBehind(uint256 performId, string memory behindAddress) public returns(uint256){
        _performBehinds[performId].push(behindAddress);

        return performId;
    }
    function getBehindList(uint256 performId) public view returns(string[] memory){
        return _performBehinds[performId];
    }
    function getTicketList(address user) public view returns(TicketInfo[] memory){
        uint256[] memory tickets = getTicketsByAccount(user);
        TicketInfo[] memory userTickets = new TicketInfo[](tickets.length);
        for(uint256 i=0; i<tickets.length ; i++){
            uint256 nowTokenId = tickets[i];
            userTickets[i] = _ticketInfos[nowTokenId];
        }
        return userTickets;
    }
    // ERC721URIStorage: TokenURI setter
    function _setTokenURI(
            uint256 tokenId, 
            string memory _tokenURI
        ) internal virtual {
        require(_exists(tokenId), "ERC721URIStorage: URI set of nonexistent token");
        _tokenURIs[tokenId] = _tokenURI;
    }
    function getTokenURI(uint256 tokenId) public view returns (string memory) {
        return _tokenURIs[tokenId];
    }
    function _setTicketInfo(
            uint256 tokenId, 
            TicketInfo memory _ticketInfo
        ) private {
        _ticketInfos[tokenId] = _ticketInfo;
    }
    function getTicketInfo(uint256 tokenId) public view returns (TicketInfo memory) {
        return _ticketInfos[tokenId];
    }
    function _setMinter(
            uint256 tokenId, 
            address minter
        ) private {
        _minters[tokenId] = minter;
    }
    function getMinter(uint256 tokenId) public view returns (address) {
        return _minters[tokenId];
    }
    function _setTicketsByAccount(
            uint256 tokenId, 
            address minter
        ) private {
        _ticketsByAccount[minter].push(tokenId);
    }
    function getTicketsByAccount(address minter) public view returns (uint256[] memory) {
        return _ticketsByAccount[minter];
    }
    function _setPerformInfo(
            uint256 performId, 
            PerformInfo memory _performInfo
        ) private {
        _performInfos[performId] = _performInfo;
    }
    function getPerformInfo(uint256 performId) public view returns (PerformInfo memory) {
        return _performInfos[performId];
    }
}
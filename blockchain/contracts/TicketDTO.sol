// SPDX-License-Identifier: MIT
pragma solidity >=0.4.22 <0.9.0;

interface TicketDTO {
    struct TicketInfo {
        string performPoster; // 공연 
        uint256 performId;
        string performName;
        string location;
        string userName;
        uint256 seatNum;
        uint256 status; // 0 : 예매완료, 1 : 취소티켓, 2 : 취소후구매된티켓
    }
    struct PerformInfo {
        uint256 id;
        address organizer;
        string title;
        string description;
        uint256 maxSeat;
        string location;
        uint256 price;
        string performDate;
        string poster;
    }
}
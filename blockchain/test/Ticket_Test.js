const Ticket = artifacts.require("Ticket");
contract('Ticket',function([deployer,user1,user2]){
    let ticket;

    beforeEach(async() => {
        ticket = await Ticket.new(); 
    })
    describe("Value", function(){
        it('check the return value', async() => {
            await ticket.create("ticketURI1", 1,"perform1","seoul","me",10,{from:deployer});
            await ticket.create("ticketURI2", 2,"perform2","daejeon","you",15,{from:deployer});
            await ticket.create("ticketURI3", 3,"perform3","bundang","nick",16,{from:deployer});
            await ticket.create("ticketURI4", 4,"perform4","suji","name",18,{from:deployer});
            
            let returnValue =  await ticket.getTicketList(deployer);

            console.log(returnValue);
            console.log(111111111);
        })
    })
});
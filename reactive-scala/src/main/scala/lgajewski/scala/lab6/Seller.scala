package lgajewski.scala.lab6

import akka.actor.{Actor, ActorLogging, Props}


class Seller(auctionNames: List[String]) extends Actor with ActorLogging {

  var counter: Int = 0

  override def receive: Receive = {
    case Action.Seller.CreateAuctions =>
      val auctions = auctionNames.map(name => context.actorOf(Props(new Auction(name))))
      auctions.foreach(auction => auction ! Action.Auction.Start)
    case Action.Auction.Sold(auction, buyer, seller, bid) =>
      log.debug(self.path.name + " > You sold an action:  " + auction.name + "! Bid: " + bid)
      log.debug(self.path.name + " > The buyer is: " + buyer + "\n")
    case Action.Done =>
      counter += 1
      if (counter == auctionNames.length) {
        context.parent ! Action.Done
      }
  }
}

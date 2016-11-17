package lgajewski.scala.lab5

import akka.actor.{Actor, Props}


class Seller(auctionNames: List[String]) extends Actor {

  override def receive: Receive = {
    case Action.Seller.CreateAuctions =>
      val auctions = auctionNames.map(name => context.actorOf(Props(new Auction(name))))
      auctions.foreach(auction => auction ! Action.Auction.Start)
    case Action.Auction.Sold(auction, buyer, seller, bid) =>
      println(self.path.name + " > You sold an action:  " + auction.name + "! Bid: " + bid)
      println(self.path.name + " > The buyer is: " + buyer + "\n")
  }
}

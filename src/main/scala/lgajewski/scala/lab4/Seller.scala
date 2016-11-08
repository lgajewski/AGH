package lgajewski.scala.lab4

import akka.actor.{Actor, Props}
import akka.event.LoggingReceive


class Seller(auctionNames: List[String]) extends Actor {

  override def receive: Receive = LoggingReceive {
    case Action.Seller.CreateAuctions =>
      auctionNames
        .map(name => context.actorOf(Props(new Auction(name))))
        .foreach(auction => auction ! Action.Auction.Start)
    case Action.Auction.Sold(auction, buyer, seller, bid) =>
      println(self.path.name + " > You sold an action:  " + auction.name + "! Bid: " + bid)
      println(self.path.name + " > The buyer is: " + buyer+  "\n")
  }
}

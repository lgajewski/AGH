package lgajewski.scala.lab3

import akka.actor.{Actor, Props}
import akka.event.LoggingReceive


class Seller(auctionNames: Set[String]) extends Actor {

  override def receive: Receive = LoggingReceive {
    case Action.Seller.CreateAuctions =>
      auctionNames
        .map(name => context.actorOf(Props[Auction], name.replaceAll(" ", "")))
        .foreach(auction => auction ! Action.Auction.Start)
    case Action.Auction.Sold(buyer, seller, bid) =>
      println(s" You sold an action:  " + sender.path.name + "! Bid: " + bid)
      println(s" The buyer is: " + buyer+  "\n")
  }
}

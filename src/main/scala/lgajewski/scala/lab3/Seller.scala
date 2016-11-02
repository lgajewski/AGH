package lgajewski.scala.lab3

import akka.actor.{Actor, Props}
import akka.event.LoggingReceive


class Seller(auctionNames: Set[String]) extends Actor {

  var auctions = auctionNames.map(name => context.actorOf(Props[Auction], name)).toList

  override def receive: Receive = LoggingReceive {
    case Action.Seller.CreateAuctions =>
      auctions.foreach(auction => auction ! Action.Auction.Start)
    case Action.Auction.Sold(buyer, seller, bid) =>
      println(s" You won " + sender.path.name + "! Bid: " + bid)
      context.system.terminate()
  }
}

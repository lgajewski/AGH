package lgajewski.scala.lab4

import akka.actor.{Actor, ActorRef}
import akka.event.LoggingReceive

import scala.collection.mutable.ListBuffer


class AuctionSearch(auctionNames: List[String]) extends Actor {

  var auctions = new ListBuffer[Auction]

  override def receive: Receive = LoggingReceive {
    case Action.AuctionSearch.Register(who) =>
      auctions += who
    case Action.AuctionSearch.Search(name) =>
      val matched = auctions.filter(auction => auction.name.toLowerCase contains name.toLowerCase).toList
      sender ! Action.AuctionSearch.SearchResult(matched)
  }
}

package lgajewski.scala.lab4

import akka.actor.Actor

import scala.collection.mutable.ListBuffer


class AuctionSearch extends Actor {

  var auctions = new ListBuffer[Auction]

  override def receive: Receive = {
    case Action.AuctionSearch.Register(who) =>
      auctions += who
    case Action.AuctionSearch.Search(name) =>
      val matched = auctions.filter(auction => auction.name.toLowerCase contains name.toLowerCase).toList
      sender ! Action.AuctionSearch.SearchResult(matched)
  }
}

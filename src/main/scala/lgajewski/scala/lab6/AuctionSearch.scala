package lgajewski.scala.lab6

import akka.actor.{Actor, ActorLogging}

import scala.collection.mutable.ListBuffer


class AuctionSearch extends Actor with ActorLogging  {

  var auctions = new ListBuffer[Auction]

  override def receive: Receive = {
    case Action.AuctionSearch.Register(who) =>
      log.debug(s"> [AUCTION SEARCH] $self - Register auction ${who.name}")
      auctions += who
    case Action.AuctionSearch.Search(name) =>
      log.debug(s"> [AUCTION SEARCH] $self - Search for $name")
      val matched = auctions.filter(auction => auction.name.toLowerCase contains name.toLowerCase).toList
      sender ! Action.AuctionSearch.SearchResult(matched)
  }
}

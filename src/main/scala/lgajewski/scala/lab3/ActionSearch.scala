package lgajewski.scala.lab3

import akka.actor.{Actor, ActorRef}
import akka.event.LoggingReceive

import scala.collection.mutable.ListBuffer


class ActionSearch(auctionNames: Set[String]) extends Actor {

  var auctions = new ListBuffer[ActorRef]

  override def receive: Receive = LoggingReceive {
    case Action.ActionSearch.Register(who) =>
      auctions += who
    case Action.ActionSearch.Search(name) =>
      println("HEY, SEARCH ME " + name)
      val matched = auctions.filter(auction => String.valueOf(auction) contains name).toList
      sender ! Action.ActionSearch.SearchResult(matched)
  }
}

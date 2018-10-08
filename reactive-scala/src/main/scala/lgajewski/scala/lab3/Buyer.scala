package lgajewski.scala.lab3

import akka.actor.{Actor, ActorSelection}
import akka.event.LoggingReceive

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Random


class Buyer(var balance: BigInt) extends Actor {
  require(balance > 0)

  var random = new Random()

  val BID_RANGE = 15
  val BID_INTERVAL = 2

  override def receive: Receive = LoggingReceive {
    case Action.Buyer.StartAuction(name) =>
      context.actorSelection("/user/ActionSearch") ! Action.AuctionSearch.Search(name)

    case Action.AuctionSearch.SearchResult(auctions) =>
      auctions.foreach(auction => auction ! Action.Auction.Bid(self, random.nextInt(BID_RANGE) + 1))

    case Action.Buyer.Bid(auction, value) if value <= balance =>
      auction ! Action.Auction.Bid(self, value)

    case Action.Auction.Bid(who, value) if who != self =>
      context.system.scheduler.scheduleOnce(random.nextInt(BID_INTERVAL) seconds, self, Action.Buyer.Bid(sender, value + random.nextInt(BID_RANGE)))

    case Action.Auction.BidFailed(current) =>
      context.system.scheduler.scheduleOnce(random.nextInt(BID_INTERVAL) seconds, self, Action.Buyer.Bid(sender, current + random.nextInt(BID_RANGE)))

    case Action.Auction.Sold(buyer, seller, bid) =>
      balance -= bid
      println(self.path.name + " > You won " + sender.path.name + "! Bid: " + bid)
      println(self.path.name + " > Your balance is: " + balance + "\n")
  }
}

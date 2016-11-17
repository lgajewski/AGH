package lgajewski.scala.lab5

import akka.actor.Actor

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Random


class Buyer(var balance: BigInt) extends Actor {
  require(balance > 0)

  var random = new Random()

  val BID_RANGE = 15
  val BID_INTERVAL = 2

  override def receive: Receive = {
    case Action.Buyer.StartAuction(name) =>
      context.actorSelection("/user/ActionSearch") ! Action.AuctionSearch.Search(name)

    case Action.AuctionSearch.SearchResult(auctions) =>
      auctions.foreach(auction => auction.ref ! Action.Auction.Bid(self, random.nextInt(BID_RANGE) + 1))

    case Action.Buyer.Bid(auction, value) if value <= balance =>
      auction ! Action.Auction.Bid(self, value)

    case Action.Auction.Bid(who, value) if who != self =>
      context.system.scheduler.scheduleOnce(random.nextInt(BID_INTERVAL) seconds, self, Action.Buyer.Bid(sender, value + random.nextInt(BID_RANGE)))

    case Action.Auction.BidFailed(current) =>
      context.system.scheduler.scheduleOnce(random.nextInt(BID_INTERVAL) seconds, self, Action.Buyer.Bid(sender, current + random.nextInt(BID_RANGE)))

    case Action.Auction.Sold(auction, buyer, seller, bid) =>
      balance -= bid
      println(self.path.name + " > You won " + auction.name + "! Bid: " + bid)
      println(self.path.name + " > Your balance is: " + balance + "\n")
  }
}

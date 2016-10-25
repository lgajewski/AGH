package lgajewski.scala.lab2

import akka.actor.{Actor, ActorRef}
import akka.event.LoggingReceive


import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

import scala.util.Random

object Buyer {

  case class Init(balance: BigInt) {
    require(balance > 0)
  }

  case class Bid(auction: ActorRef, value: BigInt) {
    require(value > 0)
  }

  case class StartAuction(auction: ActorRef)

}

class Buyer(auctions: List[ActorRef]) extends Actor {

  var random = new Random()
  var balance = BigInt(0)

  val BID_RANGE = 10
  val BID_INTERVAL = 5

  override def receive: Receive = LoggingReceive {
    case Buyer.StartAuction(auction) =>
      auction ! Auction.Start
    case Buyer.Init(value) =>
      balance = value
      auctions.foreach(auction => auction ! Auction.Bid(self, random.nextInt(BID_RANGE) + 1))
    case Buyer.Bid(auction, value) if value <= balance =>
      auction ! Auction.Bid(self, value)
    case Auction.Bid(who, value) if who != self =>
      context.system.scheduler.scheduleOnce(random.nextInt(BID_INTERVAL) seconds, self, Buyer.Bid(sender, value + random.nextInt(BID_RANGE)))
    case Auction.BidFailed(current) =>
      context.system.scheduler.scheduleOnce(random.nextInt(BID_INTERVAL) seconds, self, Buyer.Bid(sender, current + random.nextInt(BID_RANGE)))
    case Auction.Sold(buyer, seller, bid) =>
      balance -= bid
      println(s" You won " + sender.path.name + "! Bid: " + bid)
      println(s" Your balance is: " + balance + "\n")
  }
}

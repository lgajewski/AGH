package lgajewski.scala.lab2

import akka.actor.{Actor, ActorRef}
import akka.event.LoggingReceive


import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

import scala.util.Random



class Buyer(auctions: List[ActorRef]) extends Actor {

  var random = new Random()
  var balance = BigInt(0)

  val BID_RANGE = 10
  val BID_INTERVAL = 5

  override def receive: Receive = LoggingReceive {
    case Action.Buyer.StartAuction(auction) =>
      auction ! Action.Auction.Start
    case Action.Buyer.Init(value) =>
      balance = value
      auctions.foreach(auction => auction ! Action.Auction.Bid(self, random.nextInt(BID_RANGE) + 1))
    case Action.Buyer.Bid(auction, value) if value <= balance =>
      auction ! Action.Auction.Bid(self, value)
    case Action.Auction.Bid(who, value) if who != self =>
      context.system.scheduler.scheduleOnce(random.nextInt(BID_INTERVAL) seconds, self, Action.Buyer.Bid(sender, value + random.nextInt(BID_RANGE)))
    case Action.Auction.BidFailed(current) =>
      context.system.scheduler.scheduleOnce(random.nextInt(BID_INTERVAL) seconds, self, Action.Buyer.Bid(sender, current + random.nextInt(BID_RANGE)))
    case Action.Auction.Sold(buyer, seller, bid) =>
      balance -= bid
      println(s" You won " + sender.path.name + "! Bid: " + bid)
      println(s" Your balance is: " + balance + "\n")
      context.system.terminate()
  }
}

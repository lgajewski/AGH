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

}

class Buyer(auctions: List[ActorRef]) extends Actor {

  var random = new Random()
  var balance = BigInt(0)

  override def receive: Receive = LoggingReceive {
    case Buyer.Init(wallet) =>
      balance = wallet
      auctions.foreach(self ! Buyer.Bid(_, random.nextInt(10)))
    case Buyer.Bid(auction, value) =>
      auction ! Auction.Bid(self, value)
    case Auction.Bid(who, value) if who != self =>
      sender ! Auction.Bid(self, value + 10)
    case Auction.BidFailed(current) =>
      context.system.scheduler.scheduleOnce(random.nextInt(4) seconds, self, Buyer.Bid(auction, current + 10))
    case Auction.Sold(buyer, seller, bid) =>
      println(s" You won auction" + sender.path.name + "! Bid: " + bid + "\n")
      balance -= price
      println(s" Your wallet is: " + balance + "\n")
  }
}

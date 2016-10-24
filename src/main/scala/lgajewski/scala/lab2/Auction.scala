package lgajewski.scala.lab2

import akka.actor.{Actor, ActorRef, Cancellable}
import akka.event.LoggingReceive

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object Auction {

  case object Start

  case object Relist

  case class Sold(buyer: ActorRef, seller: ActorRef, bid: BigInt) {
    require(bid > 0)
  }

  case class Bid(who: ActorRef, value: BigInt) {
    require(value > 0)
  }

  case class BidFailed(current: BigInt) {
    require(current > 0)
  }

  case object BidTimerExpired

  case object DeleteTimerExpired

}

class Auction extends Actor {

  var bid = BigInt(0)
  var seller: ActorRef = null
  var buyer: ActorRef = null

  var scheduler: Cancellable = null

  override def receive: Receive = idle

  def idle: Receive = LoggingReceive {
    case Auction.Start =>
      seller = sender
      context.system.scheduler.scheduleOnce(5 seconds, self, Auction.BidTimerExpired)
      context become created
  }

  def created: Receive = LoggingReceive {
    case Auction.Bid(who, value) =>
      bid = value
      buyer ! Auction.Bid(who, value)  // inform last buyer
      buyer = sender
      context become activated
    case Auction.BidTimerExpired =>
      scheduler = context.system.scheduler.scheduleOnce(5 seconds, self, Auction.DeleteTimerExpired)
      context become ignored
  }

  def ignored: Receive = LoggingReceive {
    case Auction.Relist =>
      Option(scheduler.cancel())
      context.system.scheduler.scheduleOnce(5 seconds, self, Auction.BidTimerExpired)
      context become created
    case Auction.DeleteTimerExpired =>
      context.stop(self)
  }

  def activated: Receive = LoggingReceive {
    case Auction.Bid(who, value) if value > bid =>
      bid = value
      buyer ! Auction.Bid(who, value)  // inform last buyer
      buyer = sender
    case Auction.Bid(who, value) =>
      who ! Auction.BidFailed(bid)
    case Auction.BidTimerExpired =>
      context.system.scheduler.scheduleOnce(5 seconds, self, Auction.DeleteTimerExpired)
      buyer ! Auction.Sold(buyer, seller, bid)
      seller ! Auction.Sold(buyer, seller, bid)
      context become sold
  }

  def sold: Receive = LoggingReceive {
    case Auction.DeleteTimerExpired =>
      printf("Auction %s has been deleted from the system.", self.path.name)
      context.stop(self)
  }

}

package lgajewski.scala.lab2

import akka.actor.{Actor, ActorRef, Cancellable}
import akka.event.LoggingReceive

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Try

class Auction extends Actor {

  var bid = BigInt(0)
  var seller: ActorRef = null
  var buyer: ActorRef = null

  var scheduler: Cancellable = null

  override def receive: Receive = idle

  def idle: Receive = LoggingReceive {
    case Action.Auction.Start =>
      seller = sender
      context.system.scheduler.scheduleOnce(5 seconds, self, Action.Auction.BidTimerExpired)
      context become created
  }

  def created: Receive = LoggingReceive {
    case Action.Auction.Bid(who, value) =>
      bid = value
      Try(buyer.!(Action.Auction.Bid(who, value)))  // inform last buyer
      buyer = sender
      context become activated
    case Action.Auction.BidTimerExpired =>
      scheduler = context.system.scheduler.scheduleOnce(5 seconds, self, Action.Auction.DeleteTimerExpired)
      context become ignored
  }

  def ignored: Receive = LoggingReceive {
    case Action.Auction.Relist =>
      Option(scheduler.cancel())
      context.system.scheduler.scheduleOnce(5 seconds, self, Action.Auction.BidTimerExpired)
      context become created
    case Action.Auction.DeleteTimerExpired =>
      printf("Auction %s has been deleted from the system.\n", self.path.name)
      context.stop(self)
  }

  def activated: Receive = LoggingReceive {
    case Action.Auction.Bid(who, value) if value > bid =>
      bid = value
      buyer ! Action.Auction.Bid(who, value)  // inform last buyer
      buyer = sender
    case Action.Auction.Bid(who, value) =>
      who ! Action.Auction.BidFailed(bid)
    case Action.Auction.BidTimerExpired =>
      context.system.scheduler.scheduleOnce(5 seconds, self, Action.Auction.DeleteTimerExpired)
      buyer ! Action.Auction.Sold(buyer, seller, bid)
      seller ! Action.Auction.Sold(buyer, seller, bid)
      context become sold
  }

  def sold: Receive = LoggingReceive {
    case Action.Auction.DeleteTimerExpired =>
      printf("Auction %s has been deleted from the system.\n", self.path.name)
      context.stop(self)
  }

}

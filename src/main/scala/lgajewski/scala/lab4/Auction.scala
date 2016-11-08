package lgajewski.scala.lab4

import akka.actor.{Actor, ActorRef, Cancellable}
import akka.event.LoggingReceive
import akka.persistence.PersistentActor

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Random, Try}

class Auction(auctionName: String) extends Actor {

  var ref = self
  var bid = BigInt(0)
  var buyer: ActorRef = null
  var name: String = auctionName

  var scheduler: Cancellable = null

  val MIN_TIMEOUT = 5
  val MAX_TIMEOUT = 10
  val random = new Random()

  def getTimeout: FiniteDuration = random.nextInt(MAX_TIMEOUT - MIN_TIMEOUT) + MIN_TIMEOUT seconds


  // register in ActionSearch
  context.actorSelection("/user/ActionSearch") ! Action.AuctionSearch.Register(this)

  override def receive: Receive = idle

  def idle: Receive = LoggingReceive {
    case Action.Auction.Start =>
      // confirm that auction has just started
      context.parent ! Action.Auction.Start

      context.system.scheduler.scheduleOnce(getTimeout, self, Action.Auction.BidTimerExpired)
      context become created
  }

  def created: Receive = LoggingReceive {
    case Action.Auction.Bid(who, value) =>
      bid = value
      Try(buyer.!(Action.Auction.Bid(who, value))) // inform last buyer
      buyer = sender
      context become activated
    case Action.Auction.BidTimerExpired =>
      scheduler = context.system.scheduler.scheduleOnce(getTimeout, self, Action.Auction.DeleteTimerExpired)
      context become ignored
  }

  def ignored: Receive = LoggingReceive {
    case Action.Auction.Relist =>
      Option(scheduler.cancel())
      context.system.scheduler.scheduleOnce(getTimeout, self, Action.Auction.BidTimerExpired)
      context become created
    case Action.Auction.DeleteTimerExpired =>
      printf("Auction %s has been deleted from the system.\n", self.path.name)
      context.stop(self)
  }

  def activated: Receive = LoggingReceive {
    case Action.Auction.Bid(who, value) if value > bid =>
      bid = value
      buyer ! Action.Auction.Bid(who, value) // inform last buyer
      buyer = sender
    case Action.Auction.Bid(who, value) =>
      who ! Action.Auction.BidFailed(bid)
    case Action.Auction.BidTimerExpired =>
      context.system.scheduler.scheduleOnce(getTimeout, self, Action.Auction.DeleteTimerExpired)
      buyer ! Action.Auction.Sold(this, buyer, context.parent, bid)
      context.parent ! Action.Auction.Sold(this, buyer, context.parent, bid)
      context become sold
  }

  def sold: Receive = LoggingReceive {
    case Action.Auction.DeleteTimerExpired =>
      printf("Auction %s has been deleted from the system.\n", self.path.name)
      context.stop(self)
  }

//  override def receiveRecover: Receive = ???
//
//  override def receiveCommand: Receive = ???
//
//  override def persistenceId: String = "persistent auction" + name

}

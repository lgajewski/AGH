package lgajewski.scala.lab4

import akka.actor.{ActorRef, Cancellable}
import akka.persistence.{PersistentActor, SnapshotOffer, SnapshotSelectionCriteria}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Random, Try}

case class AuctionState(var bid: BigInt = 0, var buyer: ActorRef = null) {
  override def toString: String = "bid:" + bid
}

class Auction(auctionName: String) extends PersistentActor {

  var ref = self
  var name: String = auctionName

  var state = AuctionState()

  var scheduler: Cancellable = null

  val MIN_TIMEOUT = 5
  val MAX_TIMEOUT = 10
  val random = new Random()

  def getTimeout: FiniteDuration = random.nextInt(MAX_TIMEOUT - MIN_TIMEOUT) + MIN_TIMEOUT seconds


  // register in ActionSearch
  context.actorSelection("/user/ActionSearch") ! Action.AuctionSearch.Register(this)

  def idle: Receive = {
    case Action.Auction.Start =>
      println("> [IDLE] Action.Auction.Start " + state)
      // confirm that auction has just started
      context.parent ! Action.Auction.Start

      context.system.scheduler.scheduleOnce(getTimeout, self, Action.Auction.BidTimerExpired)
      context become created
  }

  def created: Receive = {
    case Action.Auction.Bid(who, value) if value > state.bid =>
      println("> [CREATED] Action.Auction.Bid " + state)
      state.bid = value
      Try(state.buyer.!(Action.Auction.Bid(who, value))) // inform last buyer
      state.buyer = sender
      saveSnapshot(state)
      context become activated
    case Action.Auction.Bid(who, value) =>
      who ! Action.Auction.BidFailed(state.bid)
    case Action.Auction.BidTimerExpired =>
      println("> [CREATED] Action.Auction.BidTimerExpired " + state)
      scheduler = context.system.scheduler.scheduleOnce(getTimeout, self, Action.Auction.DeleteTimerExpired)
      context become ignored
  }

  def ignored: Receive = {
    case Action.Auction.Relist =>
      Option(scheduler.cancel())
      context.system.scheduler.scheduleOnce(getTimeout, self, Action.Auction.BidTimerExpired)
      context become created
    case Action.Auction.DeleteTimerExpired =>
      println("> [IGNORED] Action.Auction.DeleteTimerExpired " + state)
      printf("> Auction %s has been deleted from the system.\n", self.path.name)
      deleteSnapshots(SnapshotSelectionCriteria())
      context.stop(self)
  }

  def activated: Receive = {
    case Action.Auction.Bid(who, value) if value > state.bid =>
      println("> [ACTIVATED] Action.Auction.Bid " + state)
      state.bid = value
      state.buyer ! Action.Auction.Bid(who, value) // inform last buyer
      state.buyer = sender
      saveSnapshot(state)
    case Action.Auction.Bid(who, value) =>
      who ! Action.Auction.BidFailed(state.bid)
    case Action.Auction.BidTimerExpired =>
      context.system.scheduler.scheduleOnce(getTimeout, self, Action.Auction.DeleteTimerExpired)
      state.buyer ! Action.Auction.Sold(this, state.buyer, context.parent, state.bid)
      context.parent ! Action.Auction.Sold(this, state.buyer, context.parent, state.bid)
      context become sold
  }

  def sold: Receive = {
    case Action.Auction.DeleteTimerExpired =>
      println("> [SOLD] Action.Auction.DeleteTimerExpired " + state)
      printf("Auction %s has been deleted from the system.\n", self.path.name)
      deleteSnapshots(SnapshotSelectionCriteria())
      context.stop(self)
  }

  override def receiveRecover: Receive = {
    case SnapshotOffer(_, snapshot: AuctionState) =>
      state = snapshot
  }

  override def receiveCommand: Receive = idle

  override def persistenceId: String = "persistent auction-" + name
}

package lgajewski.scala.lab6

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable}
import akka.event.LoggingReceive
import akka.persistence.{RecoveryCompleted, SnapshotOffer}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Try

sealed trait AuctionState

case object Idle extends AuctionState

case object Created extends AuctionState

case object Activated extends AuctionState

case object Ignored extends AuctionState

case object Sold extends AuctionState

case class StateChangeEvent(state: AuctionState)

case class State(var auctionState: AuctionState,
                 var bid: BigInt = 0,
                 var buyer: ActorRef = null,
                 var duration: Long = 0) {
  override def toString: String = "bid:" + bid + ", duration: " + duration
}

class Auction(auctionName: String) extends Actor with ActorLogging {

  var ref = self
  var name: String = auctionName
  var state = State(Idle)

  var scheduler: Cancellable = null

  def getTimeout: Long = 5000

  // register in ActionSearch
  context.actorSelection("/user/MasterSearch") ! Action.AuctionSearch.Register(this)

  def idle: Receive = LoggingReceive {
    case Action.Auction.Start =>
      log.debug("> [IDLE] Action.Auction.Start " + state)

      // confirm that auction has just started
      context.parent ! Action.Done

      changeAuctionState(Created)
  }

  def created: Receive = LoggingReceive {
    case Action.Auction.Bid(who, value) if value > state.bid =>
      log.debug("> [CREATED] Action.Auction.Bid " + state)
      Try(state.buyer.!(Action.Auction.Bid(who, value))) // inform last buyer
      updateState(Created, value, sender)
      changeAuctionState(Activated)
    case Action.Auction.Bid(who, value) =>
      who ! Action.Auction.BidFailed(state.bid)
    case Action.Auction.BidTimerExpired =>
      log.debug("> [CREATED] Action.Auction.BidTimerExpired " + state)
      changeAuctionState(Ignored)
  }

  def ignored: Receive = LoggingReceive {
    case Action.Auction.Relist =>
      changeAuctionState(Created)
    case Action.Auction.DeleteTimerExpired =>
      log.debug("> [IGNORED] Action.Auction.DeleteTimerExpired " + state)
      deleteAuction()
  }

  def activated: Receive = LoggingReceive {
    case Action.Auction.Bid(who, value) if value > state.bid =>
      log.debug("> [ACTIVATED] Action.Auction.Bid " + state)
      state.buyer ! Action.Auction.Bid(who, value) // inform last buyer
      updateState(Activated, value, sender)
    case Action.Auction.Bid(who, value) =>
      who ! Action.Auction.BidFailed(state.bid)
    case Action.Auction.BidTimerExpired =>
      state.buyer ! Action.Auction.Sold(this, state.buyer, context.parent, state.bid)
      context.parent ! Action.Auction.Sold(this, state.buyer, context.parent, state.bid)
      changeAuctionState(Sold)
  }

  def sold: Receive = LoggingReceive {
    case Action.Auction.DeleteTimerExpired =>
      log.debug("> [SOLD] Action.Auction.DeleteTimerExpired " + state)
      deleteAuction()
  }

  def updateState(auctionState: AuctionState, bid: BigInt, buyer: ActorRef): Unit = {
    state.bid = bid
    state.buyer = buyer
    state.auctionState = auctionState
    if (System.currentTimeMillis() - startTimerMillis > state.duration) {
      state.duration = System.currentTimeMillis() - startTimerMillis
    }

    // send notification
    context.actorSelection("/user/Notifier") ! Action.Notifier.Notify(auctionName, buyer, bid)
  }

  var startTimerMillis: Long = 0

  def changeAuctionState(auctionState: AuctionState): Unit = {
    log.debug("> [" + name.substring(0, 10) + "] [" + auctionState.toString + "]")
    startTimer(auctionState)
    context.become(auctionState match {
      case Idle => idle
      case Created => created
      case Activated => activated
      case Ignored => ignored
      case Sold => sold
    })
  }

  def startTimer(state: AuctionState) = {
    Try(scheduler.cancel())
    val bidTimeout = new FiniteDuration(getTimeout - this.state.duration, TimeUnit.MILLISECONDS)
    val deleteTimeout = new FiniteDuration(getTimeout, TimeUnit.MILLISECONDS)
    state match {
      case Idle =>
      case Created | Activated =>
        startTimerMillis = System.currentTimeMillis()
        scheduler = context.system.scheduler.scheduleOnce(bidTimeout, self, Action.Auction.BidTimerExpired)
      case Ignored | Sold =>
        scheduler = context.system.scheduler.scheduleOnce(deleteTimeout, self, Action.Auction.DeleteTimerExpired)
    }
  }

  def deleteAuction() = {
    log.debug(s"> Auction '$auctionName' has been deleted from the system.\n")
    context.stop(self)
  }

  val receiveRecover: Receive = LoggingReceive {
    case evt: StateChangeEvent =>
      print("> [RECOVERY] ")
      state.auctionState = evt.state
      changeAuctionState(evt.state)
    case SnapshotOffer(_, snapshot: State) =>
      log.debug("> [SNAPSHOT] load from snapshot, " + snapshot)
      state = snapshot
      changeAuctionState(state.auctionState)
    case RecoveryCompleted =>
      startTimer(state.auctionState)
      log.debug("> [RECOVERY COMPLETE]")
  }

  override def receive: Receive = idle
}

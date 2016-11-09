package lgajewski.scala.lab4

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, Cancellable}
import akka.event.LoggingReceive
import akka.persistence.{PersistentActor, RecoveryCompleted, SnapshotOffer, SnapshotSelectionCriteria}

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

class Auction(auctionName: String) extends PersistentActor {

  var ref = self
  var name: String = auctionName
  var state = State(Idle)

  var scheduler: Cancellable = null

  def getTimeout: Long = 5000

  // register in ActionSearch
  context.actorSelection("/user/ActionSearch") ! Action.AuctionSearch.Register(this)

  def idle: Receive = LoggingReceive {
    case Action.Auction.Start =>
      println("> [IDLE] Action.Auction.Start " + state)

      // confirm that auction has just started
      context.parent ! Action.Auction.Start

      persist(StateChangeEvent(Created))(event => {
        updateAuctionState(event.state)
      })
  }

  def created: Receive = LoggingReceive {
    case Action.Auction.Bid(who, value) if value > state.bid =>
      println("> [CREATED] Action.Auction.Bid " + state)
      Try(state.buyer.!(Action.Auction.Bid(who, value))) // inform last buyer
      saveSnap(Created, value, sender)
      persist(StateChangeEvent(Activated))(event => {
        updateAuctionState(event.state)
      })
    case Action.Auction.Bid(who, value) =>
      who ! Action.Auction.BidFailed(state.bid)
    case Action.Auction.BidTimerExpired =>
      println("> [CREATED] Action.Auction.BidTimerExpired " + state)
      persist(StateChangeEvent(Ignored))(event => updateAuctionState(event.state))
  }

  def ignored: Receive = LoggingReceive {
    case Action.Auction.Relist =>
      persist(StateChangeEvent(Created))(event => updateAuctionState(event.state))
    case Action.Auction.DeleteTimerExpired =>
      println("> [IGNORED] Action.Auction.DeleteTimerExpired " + state)
      deleteAuction()
  }

  def activated: Receive = LoggingReceive {
    case Action.Auction.Bid(who, value) if value > state.bid =>
      println("> [ACTIVATED] Action.Auction.Bid " + state)
      state.buyer ! Action.Auction.Bid(who, value) // inform last buyer
      saveSnap(Activated, value, sender)
    case Action.Auction.Bid(who, value) =>
      who ! Action.Auction.BidFailed(state.bid)
    case Action.Auction.BidTimerExpired =>
      state.buyer ! Action.Auction.Sold(this, state.buyer, context.parent, state.bid)
      context.parent ! Action.Auction.Sold(this, state.buyer, context.parent, state.bid)
      persist(StateChangeEvent(Sold))(event => {
        updateAuctionState(event.state)
      })
  }

  def sold: Receive = LoggingReceive {
    case Action.Auction.DeleteTimerExpired =>
      println("> [SOLD] Action.Auction.DeleteTimerExpired " + state)
      deleteAuction()
  }

  def saveSnap(auctionState: AuctionState, bid: BigInt, buyer: ActorRef): Unit = {
    state.bid = bid
    state.buyer = buyer
    state.auctionState = auctionState
    if (System.currentTimeMillis() - startTimerMillis > state.duration) {
      state.duration = System.currentTimeMillis() - startTimerMillis
    }

    saveSnapshot(state)
  }

  var startTimerMillis: Long = 0

  def updateAuctionState(auctionState: AuctionState): Unit = {
    println("> [" + name.substring(0, 10) + "] [" + auctionState.toString + "]")
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
      case Created | Activated=>
        startTimerMillis = System.currentTimeMillis()
        scheduler = context.system.scheduler.scheduleOnce(bidTimeout, self, Action.Auction.BidTimerExpired)
      case Ignored | Sold =>
        scheduler = context.system.scheduler.scheduleOnce(deleteTimeout, self, Action.Auction.DeleteTimerExpired)
    }
  }

  def deleteAuction() = {
    printf("> Auction %s has been deleted from the system.\n", self.path.name)
    deleteSnapshots(SnapshotSelectionCriteria())
    deleteMessages(Long.MaxValue)
    context.stop(self)
  }

  val receiveRecover: Receive = LoggingReceive {
    case evt: StateChangeEvent =>
      print("> [RECOVERY] ")
      state.auctionState = evt.state
      updateAuctionState(evt.state)
    case SnapshotOffer(_, snapshot: State) =>
      println("> [SNAPSHOT] load from snapshot, " + snapshot)
      state = snapshot
      updateAuctionState(state.auctionState)
    case RecoveryCompleted =>
      startTimer(state.auctionState)
      println("> [RECOVERY COMPLETE]")
  }

  override def receiveCommand: Receive = idle

  override def persistenceId: String = "persistent auction-" + name
}

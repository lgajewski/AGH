package lgajewski.scala.lab4

import akka.actor.{ActorRef, Cancellable}
import akka.persistence.{PersistentActor, SnapshotOffer, SnapshotSelectionCriteria}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Random, Try}

sealed trait AuctionState

case object Idle extends AuctionState

case object Created extends AuctionState

case object Activated extends AuctionState

case object Ignored extends AuctionState

case object Sold extends AuctionState

case class StateChangeEvent(state: AuctionState)

case class State(var bid: BigInt = 0, var buyer: ActorRef = null) {
  override def toString: String = "bid:" + bid
}

class Auction(auctionName: String) extends PersistentActor {

  var ref = self
  var name: String = auctionName

  var state = State()

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
      persist(StateChangeEvent(Created))(event => {
        updateState(event)
      })
  }

  def created: Receive = {
    case Action.Auction.Bid(who, value) if value > state.bid =>
      println("> [CREATED] Action.Auction.Bid " + state)
      state.bid = value
      Try(state.buyer.!(Action.Auction.Bid(who, value))) // inform last buyer
      state.buyer = sender
      saveSnapshot(state)
      persist(StateChangeEvent(Activated))(event => {
        updateState(event)
      })
    case Action.Auction.Bid(who, value) =>
      who ! Action.Auction.BidFailed(state.bid)
    case Action.Auction.BidTimerExpired =>
      println("> [CREATED] Action.Auction.BidTimerExpired " + state)
      persist(StateChangeEvent(Ignored))(event => updateState(event))
  }

  def ignored: Receive = {
    case Action.Auction.Relist =>
      persist(StateChangeEvent(Created))(event => updateState(event))
    case Action.Auction.DeleteTimerExpired =>
      println("> [IGNORED] Action.Auction.DeleteTimerExpired " + state)
      deleteAuction()
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
      state.buyer ! Action.Auction.Sold(this, state.buyer, context.parent, state.bid)
      context.parent ! Action.Auction.Sold(this, state.buyer, context.parent, state.bid)
      persist(StateChangeEvent(Sold))(event => {
        updateState(event)
      })
  }

  def sold: Receive = {
    case Action.Auction.DeleteTimerExpired =>
      println("> [SOLD] Action.Auction.DeleteTimerExpired " + state)
      deleteAuction()
  }

  def updateState(evt: StateChangeEvent): Unit = {
    println("> [" + name + "] [" + evt.state.toString + "]")
    context.become(evt.state match {
      case Idle => idle
      case Created =>
        Try(scheduler.cancel())
        scheduler = context.system.scheduler.scheduleOnce(getTimeout, self, Action.Auction.BidTimerExpired)
        created
      case Activated =>
        activated
      case Ignored =>
        Try(scheduler.cancel())
        scheduler = context.system.scheduler.scheduleOnce(getTimeout, self, Action.Auction.DeleteTimerExpired)
        ignored
      case Sold =>
        Try(scheduler.cancel())
        scheduler = context.system.scheduler.scheduleOnce(getTimeout, self, Action.Auction.DeleteTimerExpired)
        sold
    })
  }

  def deleteAuction() = {
    printf("> Auction %s has been deleted from the system.\n", self.path.name)
    deleteSnapshots(SnapshotSelectionCriteria())
//    deleteMessages(Long.MaxValue)
    context.stop(self)
  }

  val receiveRecover: Receive = {
    case evt: StateChangeEvent =>
      print("> [RECOVER] ")
      updateState(evt)
    case SnapshotOffer(_, snapshot: State) =>
      state = snapshot
  }

  override def receiveCommand: Receive = idle

  override def persistenceId: String = "persistent auction-" + name
}

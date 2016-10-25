package lgajewski.scala.lab2.fsm

import akka.actor.{Actor, ActorRef, Cancellable, FSM, LoggingFSM}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Try
import lgajewski.scala.lab2.Action

sealed trait State

case object Idle extends State

case object Created extends State

case object Ignored extends State

case object Activated extends State

case object Sold extends State

sealed trait Data

case object Uninitialized extends Data

class Auction extends Actor with LoggingFSM[State, Data] {

  var bid = BigInt(0)
  var seller: ActorRef = null
  var buyer: ActorRef = null

  var scheduler: Cancellable = null

  startWith(Idle, Uninitialized)

  when(Idle) {
    case Event(Action.Auction.Start, _) =>
      seller = sender
      goto(Created)
  }

  when(Created, stateTimeout = 5 seconds) {
    case Event(Action.Auction.Bid(who, value), Uninitialized) =>
      bid = value
      Try(buyer.!(Action.Auction.Bid(who, value))) // inform last buyer
      buyer = sender
      goto(Activated)
    case Event(StateTimeout, Uninitialized) =>
      goto(Ignored)
  }

  when(Ignored, stateTimeout = 5 seconds) {
    case Event(Action.Auction.Relist, Uninitialized) =>
      goto(Created)
    case Event(StateTimeout, Uninitialized) =>
      printf("Auction %s has been deleted from the system.\n", self.path.name)
      stop()
  }

  when(Activated, stateTimeout = 5 seconds) {
    case Event(Action.Auction.Bid(who, value), Uninitialized) if value > bid =>
      bid = value
      buyer ! Action.Auction.Bid(who, value) // inform last buyer
      buyer = sender
      stay()
    case Event(Action.Auction.Bid(who, value), Uninitialized) =>
      who ! Action.Auction.BidFailed(bid)
      stay()
    case Event(StateTimeout, Uninitialized) =>
      buyer ! Action.Auction.Sold(buyer, seller, bid)
      seller ! Action.Auction.Sold(buyer, seller, bid)
      goto(Sold)
  }

  when(Sold, stateTimeout = 5 seconds) {
    case Event(StateTimeout, Uninitialized) =>
      printf("Auction %s has been deleted from the system.\n", self.path.name)
      stop()
  }

  initialize()
}

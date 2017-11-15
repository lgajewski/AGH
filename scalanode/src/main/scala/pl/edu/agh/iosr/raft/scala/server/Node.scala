package pl.edu.agh.iosr.raft.scala.server

import akka.actor.{ActorLogging, ActorRef, FSM, Props}

import scala.concurrent.duration._
import scala.util.Random

class Node(others: ActorRef) extends FSM[State, Data] with ActorLogging {

  startWith(Idle, Uninitialized)

  when(Idle, stateTimeout = 150 + new Random().nextInt(150) millisecond) {
    case Event(StateTimeout, _) => goto(Candidate) using ElectionState()
    case Event(RequestVote, _) => goto(Follower)
  }

  onTransition {
    case Idle -> Candidate =>
      log.info(String.format("Node [%s] is now a CANDIDATE", self.path.name))
      others ! RequestVote

    case Idle -> Follower =>
      log.info(String.format("Node [%s] is now a FOLLOWER", self.path.name))
  }

  when(Candidate) {
    case Event(_, _) => stay
  }

  when(Follower) {
    case Event(_, _) => stay
  }

  initialize()
}

object Node {
  def props(server: ActorRef): Props = Props(new Node(server))
}

//states
sealed trait State
case object Idle extends State
case object Follower extends State
case object Candidate extends State
case object Leader extends State

//data
sealed trait Data
case object Uninitialized extends Data
case class ElectionState(term: Int = 0, voteCount: Int = 0) extends Data
case class ElectionTimeout(timeout: Long) extends Data

//events
case object RequestVote
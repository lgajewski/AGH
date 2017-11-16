package pl.edu.agh.iosr.raft.scala.server

import akka.actor.{ActorLogging, ActorRef, FSM, Props}

import scala.concurrent.duration._
import scala.util.Random

class Node(others: ActorRef) extends FSM[State, Data] with ActorLogging {

  startWith(Idle, ElectionState())

  private val random = new Random()

  private def randomTimeout = {
    150 + random.nextInt(750)
  }

  when(Idle, stateTimeout = randomTimeout millisecond) {
    case Event(StateTimeout, _) => goto(Candidate) using ElectionState()
    case Event(RequestVote(_), AlreadyVoted(_, _)) => stay
    case Event(RequestVote(ref), _) =>
      ref ! Vote
      stateData match {
        case ElectionState(term, gatheredVotes) => goto(Idle) using AlreadyVoted(term, gatheredVotes)
      }
    case Event(Obey, AlreadyVoted(term, _)) => goto(Follower) using ElectionState(term + 1)
    case Event(Obey, ElectionState(term, _)) => goto(Follower) using ElectionState(term + 1)
  }

  onTransition {
    case state -> Candidate if state != Candidate =>
      log.info(s"Node [${self.path.name}] is now a CANDIDATE from $state")
      self ! Vote
      others ! RequestVote(self)

    case state -> Follower => stateData match {
      case ElectionState(term, gatheredVotes)  =>
        log.info(s"Node [${self.path.name}] is now a FOLLOWER from $state (term: $term, gathered votes: $gatheredVotes)")
      case AlreadyVoted(term, gatheredVotes) =>
        log.info(s"Node [${self.path.name}] is now a FOLLOWER from $state (term: $term, gathered votes: $gatheredVotes)")
      case _ =>
        log.error("Must not occur")
        log.error(stateData.toString)
        throw new RuntimeException
    }

    case Candidate -> Leader =>
      stateData match {
        case ElectionState(term, gatheredVotes) =>
          log.info(s"Node [${self.path.name}] is now a LEADER (term: $term, gathered votes: $gatheredVotes)")
      }
      others ! Obey

    case Leader -> Leader =>
      others ! HeartBeat
//      log.debug("HEARTBEAT sent")
  }

  when(Candidate) {
    case Event(Vote, ElectionState(term, gatheredVotes)) =>
      others ! AmILeader(gatheredVotes + 1)
      goto(Candidate) using ElectionState(term, gatheredVotes + 1)
    case Event(BecomeLeader, _) => goto(Leader)
    case Event(RequestVote(_), _) => stay
    case Event(Obey, AlreadyVoted(term, _)) => goto(Follower) using ElectionState(term + 1)
    case Event(Obey, ElectionState(term, _)) => goto(Follower) using ElectionState(term + 1)
  }

  when(Follower, stateTimeout = randomTimeout millisecond) {
    case Event(StateTimeout, ElectionState(term, _)) => goto (Candidate) using ElectionState(term + 1)
    case Event(Vote, ElectionState(term, gatheredVotes)) =>
      others ! AmILeader(gatheredVotes + 1)
      goto(Candidate) using ElectionState(term, gatheredVotes + 1)
    case Event(RequestVote(ref), ElectionState(term, _)) =>
      ref ! Vote
      goto(Follower) using ElectionState(term + 1)
    case Event(HeartBeat, _) => stay
  }

  when(Leader, stateTimeout = 50 millisecond) {
    case Event(StateTimeout, _) => goto(Leader)
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
case class ElectionState(term: Int = 0, voteCount: Int = 0) extends Data
case class ElectionTimeout(timeout: Long) extends Data
case class AlreadyVoted(term: Int = 0, voteCount: Int = 0) extends Data

//events
case class RequestVote(ref: ActorRef)
case object Vote
case class AmILeader(gatheredVotes: Int)
case object BecomeLeader
case object Obey
case class HeartBeat(value: Option[Int] = None)
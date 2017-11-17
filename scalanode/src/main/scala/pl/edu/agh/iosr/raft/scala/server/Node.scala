package pl.edu.agh.iosr.raft.scala.server

import akka.actor.{ActorLogging, ActorRef, FSM, Props}

import scala.concurrent.duration._
import scala.util.Random

class Node(server: ActorRef) extends FSM[State, Data] with ActorLogging {

  startWith(Idle, NodeState())

  private val random = new Random()

  private def randomTimeout = {
    150 + random.nextInt(150) millisecond
  }

  when(Idle, stateTimeout = randomTimeout) {
    case Event(StateTimeout, _) => goto(Candidate) using NodeState()
    case Event(RequestVote(_), AlreadyVoted(_, _, _)) => stay
    case Event(RequestVote(ref), _) =>
      ref ! Vote
      stateData match {
        case NodeState(term, gatheredVotes, log) => goto(Idle) using AlreadyVoted(term, gatheredVotes, log)
      }
    case Event(Obey, AlreadyVoted(term, _, log)) => goto(Follower) using NodeState(term = term + 1, log = log)
    case Event(Obey, s@NodeState(term, _, _)) => goto(Follower) using s.copy(term = term + 1)
  }

  onTransition {
    case state -> Candidate if state != Candidate =>
      log.info(s"Node [${self.path.name}] is now a CANDIDATE from $state")
      self ! Vote
      server ! RequestVote(self)

    case state -> Follower => stateData match {
      case NodeState(term, gatheredVotes, _)  =>
        log.info(s"Node [${self.path.name}] is now a FOLLOWER from $state (term: $term, gathered votes: $gatheredVotes)")
      case AlreadyVoted(term, gatheredVotes, _) =>
        log.info(s"Node [${self.path.name}] is now a FOLLOWER from $state (term: $term, gathered votes: $gatheredVotes)")
      case _ =>
        log.error("Must not occur")
        log.error(stateData.toString)
        throw new RuntimeException
    }

    case Candidate -> Leader =>
      stateData match {
        case NodeState(term, gatheredVotes, _) =>
          log.info(s"Node [${self.path.name}] is now a LEADER (term: $term, gathered votes: $gatheredVotes)")
      }
      server ! Obey

    case Leader -> Leader =>
      server ! HeartBeat
//      log.debug("HEARTBEAT sent")
  }

  when(Candidate, stateTimeout = randomTimeout) {
    case Event(Vote, s@NodeState(_, gatheredVotes, _)) =>
      server ! AmILeader(gatheredVotes + 1)
      goto(Candidate) using s.copy(voteCount = gatheredVotes + 1)
    case Event(BecomeLeader, _) => goto(Leader)
    case Event(RequestVote(_), _) => stay
    case Event(Obey, AlreadyVoted(term, _, log)) => goto(Follower) using NodeState(term = term + 1, log = log)
    case Event(Obey, s@NodeState(term, _, _)) => goto(Follower) using s.copy(term = term + 1)
    case Event(StateTimeout, s@NodeState(term, _, _)) => goto (Candidate) using s.copy(term = term + 1)
  }

  when(Follower, stateTimeout = randomTimeout) {
    case Event(StateTimeout, s@NodeState(term, _, _)) => goto (Candidate) using s.copy(term = term + 1)
    case Event(Vote, s@NodeState(_, gatheredVotes, _)) =>
      server ! AmILeader(gatheredVotes + 1)
      goto(Candidate) using s.copy(voteCount = gatheredVotes + 1)
    case Event(RequestVote(ref), s@NodeState(term, _, _)) =>
      ref ! Vote
      goto(Follower) using s.copy(term = term + 1)
    case Event(HeartBeat, _) => stay
  }

  when(Leader, stateTimeout = 20 millisecond) {
    case Event(StateTimeout, _) => goto(Leader)
    case Event(AppendEntryClient(v), _) if sender() != self =>
      server ! AppendEntry(v)
      stay
    case Event(HeartBeat | BecomeLeader | Obey | Vote, _) => stay
    case Event(Ack, _) => stay //todo
  }

  whenUnhandled {
    case Event(AppendEntry(v), s@NodeState(_, _, log)) =>
      sender() ! Ack
      stay using s.copy(log = v :: log)
    case Event(ShowStatus, _) =>
      server ! (stateName, stateData)
      stay
    case Event(AppendEntryClient(_), _) => stay
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
case class NodeState(term: Int = 0, voteCount: Int = 0, log: List[Int] = Nil) extends Data
case class AlreadyVoted(term: Int = 0, voteCount: Int = 0, log: List[Int] = Nil) extends Data

//events
case class RequestVote(ref: ActorRef)
case object Vote
case class AmILeader(gatheredVotes: Int)
case object BecomeLeader
case object Obey
case object HeartBeat
case class AppendEntry(value: Int)
case class AppendEntryClient(value: Int)
case object Ack
case object ShowStatus
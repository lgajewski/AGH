package pl.edu.agh.iosr.raft.scala.server

import akka.actor.{Actor, ActorLogging}
import akka.routing.{ActorRefRoutee, BroadcastRoutingLogic, Router}

class Server(nodeCount: Int = 5) extends Actor with ActorLogging {

  private val nodes = Vector.tabulate(nodeCount) { i =>
    context.actorOf(Node.props(this.self), "NODE_" + (i + 1))
  }

  val broadcastRouter: Router = Router(BroadcastRoutingLogic(), nodes.map(ActorRefRoutee))

  override def receive: Receive = {
    case AmILeader(gatheredVotes) if gatheredVotes > nodeCount / 2 => sender() ! BecomeLeader
    case AmILeader(_) =>
    case m => broadcastRouter.route(m, sender())
  }
}

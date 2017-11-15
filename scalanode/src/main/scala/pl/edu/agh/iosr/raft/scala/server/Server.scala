package pl.edu.agh.iosr.raft.scala.server

import akka.actor.{Actor, ActorLogging, ActorSystem}
import akka.routing.{ActorRefRoutee, BroadcastRoutingLogic, Router}

class Server(nodeCount: Int = 5) extends Actor with ActorLogging {

  final val system = ActorSystem("iosr-raft")

  private val nodes = Vector.tabulate(nodeCount) { i =>
    system.actorOf(Node.props(this.self), "NODE_" + (i + 1))
  }

  val broadcastRouter: Router = Router(BroadcastRoutingLogic(), nodes.map(ActorRefRoutee))

  override def receive: Receive = {
    case m => broadcastRouter.route(m, sender())
  }
}

package pl.edu.agh.iosr.raft.scala.server

import akka.actor.{Actor, ActorLogging, ActorSystem}
import akka.routing.{ActorRefRoutee, BroadcastRoutingLogic, Router}

class Server extends Actor with ActorLogging {
  val system = ActorSystem("iosr-raft")

  val broadcastRouter: Router = {
    val nodes = Vector.tabulate(5) { i =>
      ActorRefRoutee(system.actorOf(Node.props(this), "NODE_" + (i + 1)))
    }
    Router(BroadcastRoutingLogic(), nodes)
  }

  override def receive: Receive = {
    case m =>
      broadcastRouter.route(m, sender())
  }
}

package pl.edu.agh.iosr.raft.scala.server

import akka.actor.{Actor, ActorLogging, Props}

class Node(server: Server) extends Actor with ActorLogging {

  override def receive(): Receive = {
    case m =>
      log.info("Message received: " + m)
      server.self ! "GOT"
  }
}

object Node {
  def props(server: Server): Props = Props(new Node(server))
}

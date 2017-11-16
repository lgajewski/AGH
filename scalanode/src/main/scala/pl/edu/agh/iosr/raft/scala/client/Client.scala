package pl.edu.agh.iosr.raft.scala.client

import akka.actor.{Actor, ActorLogging, ActorRef}

class Client(server: ActorRef) extends Actor with ActorLogging {

  override def receive: Receive = {
    case _  =>
  }
}

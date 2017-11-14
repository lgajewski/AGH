package pl.edu.agh.iosr.raft.scala

import akka.actor.{ActorSystem, Props}
import pl.edu.agh.iosr.raft.scala.server.Server

object RaftMain extends App {

  val system = ActorSystem("iosr-raft")

  val server = system.actorOf(Props[Server])

  server ! "test"
}

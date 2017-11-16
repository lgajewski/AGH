package pl.edu.agh.iosr.raft.scala

import akka.actor.{ActorSystem, Props}
import pl.edu.agh.iosr.raft.scala.client.Client
import pl.edu.agh.iosr.raft.scala.server.Server

object RaftMain extends App {
  val system = ActorSystem("iosr-raft-main")

  val server = system.actorOf(Props(new Server(3)), "server")
  val client = system.actorOf(Props(new Client(server)), "client")
}

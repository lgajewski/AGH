package pl.edu.agh.iosr.raft.scala

import java.io.File

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import pl.edu.agh.iosr.raft.scala.server.Server

object RaftMain extends App {
  val configFile = getClass.getClassLoader.
    getResource("server.conf").getFile
  val config = ConfigFactory.parseFile(new File(configFile))
  val system = ActorSystem("iosr-raft-main", config)

  val server = system.actorOf(Props(new Server(3)), "server")
}

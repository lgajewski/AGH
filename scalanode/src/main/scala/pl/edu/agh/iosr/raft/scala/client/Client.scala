package pl.edu.agh.iosr.raft.scala.client

import java.io.File

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import pl.edu.agh.iosr.raft.scala.server.{AppendEntryClient, ShowStatus}

class Client() extends Actor with ActorLogging {

  private val server = context.actorSelection("akka.tcp://iosr-raft-main@127.0.0.1:11223/user/server")

  override def receive: Receive = {
    case m => server ! m
  }
}

object Client extends App {
  val configFile = getClass.getClassLoader.getResource("client.conf").getFile
  val config = ConfigFactory.parseFile(new File(configFile))
  val system = ActorSystem("ClientSystem", config)
  val client = system.actorOf(Props[Client], name="client")

  Iterator.continually(scala.io.StdIn.readLine()).foreach {
    case "status" => client ! ShowStatus
    case Int(x) => client ! AppendEntryClient(x)
  }
}

object Int {
  def unapply(s : String) : Option[Int] = try {
    Some(s.toInt)
  } catch {
    case _ : java.lang.NumberFormatException => None
  }
}
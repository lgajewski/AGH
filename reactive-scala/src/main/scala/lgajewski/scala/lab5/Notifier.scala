package lgajewski.scala.lab5

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, OneForOneStrategy, Props}

import scala.concurrent.duration._

class Notifier extends Actor {

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = Int.MaxValue, withinTimeRange = 1 minute) {
      case _ => Restart
    }

  override def receive: Receive = {
    case Action.Notifier.Notify(title, buyer, bid) =>
      println("> [NOTIFIER] Trying to send a NotifierRequest...")
      // create request and delegate
      context.actorOf(Props[NotifierRequest]) ! Action.Notifier.Notify(title, buyer, bid)
  }
}

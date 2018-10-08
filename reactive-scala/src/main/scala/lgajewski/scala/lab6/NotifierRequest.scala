package lgajewski.scala.lab6

import akka.actor.{Actor, ActorLogging}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration._

class NotifierRequest extends Actor with ActorLogging  {
  override def receive: Receive = {
    case Action.Notifier.Notify(title, buyer, bid) =>
      // use remote account
      val publisher = context.actorSelection("akka.tcp://AuctionSystem@127.0.0.1:2553/user/AuctionPublisher")
      implicit val timeout = Timeout(2 seconds)

      // use ask pattern
      val future = publisher ? Action.Notifier.Notify(title, buyer, bid)

      try {
        Await.result(future, timeout.duration)
      } catch {
        case e: Exception => log.debug("> [REQUEST]" + e.getMessage)
      }

      sender ! Action.Notifier.Done
  }
}

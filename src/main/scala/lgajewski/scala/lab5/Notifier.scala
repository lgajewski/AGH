package lgajewski.scala.lab5

import akka.actor.Actor
import akka.util.Timeout
import akka.pattern.ask

import scala.concurrent.Await
import scala.concurrent.duration._

class Notifier extends Actor {
  override def receive: Receive = {
    case Action.Notifier.Notify(title, buyer, bid) =>
      // use remote account
      val publisher = context.actorSelection("akka.tcp://AuctionSystem@127.0.0.1:2552/user/AuctionPublisher")
      implicit val timeout = Timeout(5 seconds)

      // use ask pattern
      val future = publisher ? Action.Notifier.Notify(title, buyer, bid)
      val result = Await.result(future, timeout.duration)

      sender ! Action.Notifier.Done
  }
}

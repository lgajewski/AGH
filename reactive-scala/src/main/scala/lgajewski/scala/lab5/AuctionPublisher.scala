package lgajewski.scala.lab5

import akka.actor.Actor

class AuctionPublisher extends Actor {
  override def receive: Receive = {
    case Action.Notifier.Notify(title, buyer, bid) =>
      println(s"> [AUCTION PUBLISHER] [$title] - ${buyer.path.name}, bid: $bid")

      sender ! Action.Notifier.Done
  }
}

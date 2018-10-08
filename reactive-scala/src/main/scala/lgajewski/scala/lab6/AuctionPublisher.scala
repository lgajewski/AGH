package lgajewski.scala.lab6

import akka.actor.{Actor, ActorLogging}

class AuctionPublisher extends Actor with ActorLogging {
  override def receive: Receive = {
    case Action.Notifier.Notify(title, buyer, bid) =>
      log.debug(s"> [AUCTION PUBLISHER] [$title] - ${buyer.path.name}, bid: $bid")

      sender ! Action.Notifier.Done
  }
}

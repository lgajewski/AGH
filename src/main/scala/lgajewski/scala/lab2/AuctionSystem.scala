package lgajewski.scala.lab2

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.event.LoggingReceive

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Auction {

  case class Start(seller: ActorRef)

  case class Bid(value: BigInt) {
    require(value > 0)
  }

  case object Done

  case object Failed

}

class Auction extends Actor {

  var bid = BigInt(0)

  override def receive: Receive = idle

  def idle: Receive = LoggingReceive {
    case Auction.Start =>
      // TODO start bidTimer
      context become created
  }

  def created: Receive = LoggingReceive {
    case Auction.Bid(value) if value > bid =>
      bid = value
      sender ! Auction.Done
    case _ =>
      sender ! Auction.Failed
  }

}

class Buyer extends Actor {
  override def receive: Receive = LoggingReceive {
    case _ => sender ! "FAILED"
  }
}

object AuctionSystem extends App {
  val system = ActorSystem("AuctionSystem")
  val auction = system.actorOf(Props[Auction], "auction")

  auction ! Auction.Bid(5)

  Await.result(system.whenTerminated, Duration.Inf)
}

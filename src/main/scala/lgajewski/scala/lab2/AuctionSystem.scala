package lgajewski.scala.lab2

import akka.actor.{Actor, ActorRef, ActorSystem, Cancellable, Props}
import akka.event.LoggingReceive

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object AuctionSystem extends App {
  val system = ActorSystem("AuctionSystem")

  val auction = system.actorOf(Props[Auction], "auction")

  val auctions: List[ActorRef] = List(auction)

  val buyer = system.actorOf(Props(new Buyer(auctions)), "buyer")

  Await.result(system.whenTerminated, Duration.Inf)
}

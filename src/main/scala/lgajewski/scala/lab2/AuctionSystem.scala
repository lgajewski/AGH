package lgajewski.scala.lab2

import akka.actor.{Actor, ActorRef, ActorSystem, Cancellable, Props}
import akka.event.LoggingReceive

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object AuctionSystem extends App {
  val system = ActorSystem("AuctionSystem")

  val auction = system.actorOf(Props[Auction], "auction")
  val auctions: List[ActorRef] = List(auction)

  val buyer1 = system.actorOf(Props(new Buyer(auctions)), "buyer1")
  val buyer2 = system.actorOf(Props(new Buyer(auctions)), "buyer2")
  val buyer3 = system.actorOf(Props(new Buyer(auctions)), "buyer3")

  auction ! Auction.Start

  buyer1 ! Buyer.Init(20)
  buyer2 ! Buyer.Init(160)
  buyer3 ! Buyer.Init(100)

  Await.result(system.whenTerminated, Duration.Inf)
}

package lgajewski.scala.lab3

import akka.actor.{ActorRef, ActorSystem, Props}
import lgajewski.scala.lab2.fsm.{Auction => AuctionFSM}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object AuctionSystem extends App {
  val system = ActorSystem("AuctionSystem")

//  val auction = system.actorOf(Props[AuctionFSM], "auction")
  val auction = system.actorOf(Props[Auction], "auction")

  val auctions: List[ActorRef] = List(auction)

  val buyer1 = system.actorOf(Props(new Buyer(auctions)), "buyer1")
  val buyer2 = system.actorOf(Props(new Buyer(auctions)), "buyer2")
  val buyer3 = system.actorOf(Props(new Buyer(auctions)), "buyer3")

  auction ! Action.Auction.Start

  buyer1 ! Action.Buyer.Init(20)
  buyer2 ! Action.Buyer.Init(60)
  buyer3 ! Action.Buyer.Init(80)

  Await.result(system.whenTerminated, Duration.Inf)
}

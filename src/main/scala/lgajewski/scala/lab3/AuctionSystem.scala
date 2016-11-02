package lgajewski.scala.lab3

import akka.actor.{ActorRef, ActorSystem, Props}
import lgajewski.scala.lab2.fsm.{Auction => AuctionFSM}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object AuctionSystem extends App {
  val system = ActorSystem("AuctionSystem")

  val auctionNames = Set("Audi A6 diesel manual", "Audi A4 gas auto", "BMW X3 diesel auto")
  val actionSearch = system.actorOf(Props(new ActionSearch(auctionNames)), "ActionSearch")

  val seller = system.actorOf(Props(new Seller(auctionNames)), "Seller")

  val buyer1 = system.actorOf(Props(new Buyer(20)), "buyer1")
  val buyer2 = system.actorOf(Props(new Buyer(60)), "buyer2")
  val buyer3 = system.actorOf(Props(new Buyer(80)), "buyer3")

  seller ! Action.Seller.CreateAuctions


  Thread.sleep(1500)
  buyer1 ! Action.Buyer.StartAuction("Audi")

  Await.result(system.whenTerminated, Duration.Inf)
}

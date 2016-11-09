package lgajewski.scala.lab4

import akka.actor.{ActorSystem, Props}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/*object AuctionSystem extends App {
  val system = ActorSystem("AuctionSystem")

  val auctionNames = List("Audi A6 diesel manual", "Peugeot 307 gas manual", "BMW X3 diesel automatic", "Audi A3 gas manual")
  val auctionNames = List("BMW X3 diesel automatic")
  val actionSearch = system.actorOf(Props(new AuctionSearch(auctionNames)), "ActionSearch")

  val seller = system.actorOf(Props(new Seller(auctionNames)), "Seller")
  seller ! Action.Seller.CreateAuctions

  Thread.sleep(1500)

  val buyer1 = system.actorOf(Props(new Buyer(60)), "buyer1")
  val buyer2 = system.actorOf(Props(new Buyer(100)), "buyer2")
  val buyer3 = system.actorOf(Props(new Buyer(120)), "buyer3")

  buyer1 ! Action.Buyer.StartAuction("automatic")
  buyer2 ! Action.Buyer.StartAuction("automatic")
  buyer3 ! Action.Buyer.StartAuction("automatic")

  Await.result(system.whenTerminated, Duration.Inf)
}*/

object AuctionSystem extends App {
  val system = ActorSystem("AuctionSystem")

  val auctionSearch = system.actorOf(Props[AuctionSearch], "ActionSearch")

  val auction = system.actorOf(Props(new Auction("AUDI")), "audi")

  auction ! Action.Auction.Start

  Thread.sleep(1000)

  auction ! Action.Auction.Bid(system.deadLetters, 50)

  Thread.sleep(4000)

  auctionSearch ! Action.AuctionSearch.Search("audi")

  Await.result(system.whenTerminated, Duration.Inf)
}

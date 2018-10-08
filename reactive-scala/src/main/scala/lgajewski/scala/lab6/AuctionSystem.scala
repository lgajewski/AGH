package lgajewski.scala.lab6

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object AuctionSystem extends App {
  val config = ConfigFactory.load()
  val localSystem = ActorSystem("AuctionSystem")
  val remoteSystem = ActorSystem("AuctionSystem", config.getConfig("remote").withFallback(config))

//  val auctionNames = List("BMW X3 diesel automatic")
  val auctionNames = List("Audi A6 diesel manual", "Peugeot 307 gas manual", "BMW X3 diesel automatic", "Audi A3 gas manual")

  val masterSearch = localSystem.actorOf(Props[MasterSearch], "MasterSearch")

  val publisher = remoteSystem.actorOf(Props[AuctionPublisher], "AuctionPublisher")
  val notifier = localSystem.actorOf(Props[Notifier], "Notifier")

  val seller = localSystem.actorOf(Props(new Seller(auctionNames)), "Seller")
  seller ! Action.Seller.CreateAuctions

  Thread.sleep(1500)

  val buyer1 = localSystem.actorOf(Props(new Buyer(600)), "buyer1")
  val buyer2 = localSystem.actorOf(Props(new Buyer(1000)), "buyer2")
  val buyer3 = localSystem.actorOf(Props(new Buyer(1200)), "buyer3")

  buyer1 ! Action.Buyer.StartAuction("manual")
  buyer2 ! Action.Buyer.StartAuction("manual")
  buyer3 ! Action.Buyer.StartAuction("automatic")

  Await.result(localSystem.whenTerminated, Duration.Inf)
}

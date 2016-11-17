package lgajewski.scala.lab5

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object AuctionSystem extends App {
  val config = ConfigFactory.load()
  val clientSystem = ActorSystem("AuctionSystem", config.getConfig("clientapp").withFallback(config))
  val serverSystem = ActorSystem("AuctionSystem", config.getConfig("serverapp").withFallback(config))

  val auctionNames = List("BMW X3 diesel automatic")
//  val auctionNames = List("Audi A6 diesel manual", "Peugeot 307 gas manual", "BMW X3 diesel automatic", "Audi A3 gas manual")

  val actionSearch = clientSystem.actorOf(Props[AuctionSearch], "ActionSearch")

  val publisher = serverSystem.actorOf(Props[AuctionPublisher], "AuctionPublisher")
  val notifier = clientSystem.actorOf(Props[Notifier], "Notifier")

  val seller = clientSystem.actorOf(Props(new Seller(auctionNames)), "Seller")
  seller ! Action.Seller.CreateAuctions

  Thread.sleep(1500)

  val buyer1 = clientSystem.actorOf(Props(new Buyer(60)), "buyer1")
  val buyer2 = clientSystem.actorOf(Props(new Buyer(100)), "buyer2")
  val buyer3 = clientSystem.actorOf(Props(new Buyer(120)), "buyer3")

  buyer1 ! Action.Buyer.StartAuction("automatic")
  buyer2 ! Action.Buyer.StartAuction("automatic")
  buyer3 ! Action.Buyer.StartAuction("automatic")

  Await.result(clientSystem.whenTerminated, Duration.Inf)
}

package lgajewski.scala.lab3

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class SellerSpec extends TestKit(ActorSystem("SellerSpec"))
  with WordSpecLike with BeforeAndAfterAll with ImplicitSender {

  override def afterAll(): Unit = {
    system.terminate
  }

  val auctionNames = Set("Audi A6 diesel manual", "Peugeot 307 gas manual", "BMW X3 diesel automatic", "Audi A3 gas manual")

  "A Seller" must {

    "test auctions as children" in {
      val parent = TestProbe()
      val auction1 = parent.childActorOf(Props[Auction])
      val auction2 = parent.childActorOf(Props[Auction])

      parent.send(auction1, Action.Auction.Start)
      parent.send(auction2, Action.Auction.Start)

      parent.expectMsg(Action.Auction.Start)
      parent.expectMsg(Action.Auction.Start)
    }
  }
}

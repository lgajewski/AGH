package lgajewski.scala.lab3

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class AuctionSpec extends TestKit(ActorSystem("AuctionSpec"))
  with WordSpecLike with BeforeAndAfterAll with ImplicitSender {

  override def afterAll(): Unit = {
    system.terminate
  }

  "A Auction" must {

    "confirm that is started" in {
      val parent = TestProbe()
      val auction1 = parent.childActorOf(Props[Auction])

      parent.send(auction1, Action.Auction.Start)
      parent.expectMsg(Action.Auction.Start)
    }

    "accepts every bid, which is first" in {
      val auction = system.actorOf(Props[Auction])

      auction ! Action.Auction.Start
      auction ! Action.Auction.Bid(self, 1)
      auction ! Action.Auction.Bid(self, 1)

      expectMsg(Action.Auction.BidFailed(1))
    }

    "hold a guard on bid in activated state" in {
      val buyer1 = TestProbe("buyer1")
      val buyer2 = TestProbe("buyer2")

      val auction = system.actorOf(Props[Auction])

      auction ! Action.Auction.Start
      auction ! Action.Auction.Bid(buyer1.ref, 2)
      auction ! Action.Auction.Bid(buyer2.ref, 7)
      auction ! Action.Auction.Bid(buyer1.ref, 5)
      auction ! Action.Auction.Bid(buyer1.ref, 6)

      buyer1.expectMsg(Action.Auction.BidFailed(7))
      buyer1.expectMsg(Action.Auction.BidFailed(7))
    }
  }

}

package lgajewski.scala.lab3

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class BuyerSpec extends TestKit(ActorSystem("BuyerSpec"))
  with WordSpecLike with BeforeAndAfterAll with ImplicitSender {

  override def afterAll(): Unit = {
    system.terminate
  }

  "A Buyer" must {

    "initial bid every action on search result" in {
      val auction1 = TestProbe("AudiA6dieselmanual")
      val auction2 = TestProbe("Peugeot307gasmanual")

      val buyer = system.actorOf(Props(new Buyer(40)))

      buyer ! Action.AuctionSearch.SearchResult(List(auction1.ref, auction2.ref))

      auction1.expectMsgPF() {
        case Action.Auction.Bid(b, bid) =>
          assert(buyer == b)
          assert(bid > 0)
      }

      auction2.expectMsgPF() {
        case Action.Auction.Bid(b, bid) =>
          assert(buyer == b)
          assert(bid > 0)
      }
    }

    "dont bid when no enough money" in {
      val auction1 = TestProbe("AudiA6dieselmanual")

      val buyer = system.actorOf(Props(new Buyer(40)))

      buyer ! Action.Buyer.Bid(auction1.ref, 80)

      auction1.expectNoMsg()
    }

    "reduce balance when auction sold" in {
      val auction1 = TestProbe("AudiA6dieselmanual")
      val auction2 = TestProbe("OtherAuction")

      val buyer = system.actorOf(Props(new Buyer(40)))

      buyer ! Action.Auction.Sold(buyer, TestProbe().ref, 20)
      buyer ! Action.Buyer.Bid(auction2.ref, 25)

      auction2.expectNoMsg()
    }

  }

}

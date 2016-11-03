package lgajewski.scala.lab3

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class AuctionSearchSpec extends TestKit(ActorSystem("AuctionSearchSpec"))
  with WordSpecLike with BeforeAndAfterAll with ImplicitSender {

  override def afterAll(): Unit = {
    system.terminate
  }

  val auctionNames = Set("Audi A6 diesel manual", "Peugeot 307 gas manual", "BMW X3 diesel automatic", "Audi A3 gas manual")

  "A AuctionSearch" must {

    "register auctions" in {
      val auction1 = TestProbe("AudiA6dieselmanual")

      val auctionSearch = system.actorOf(Props(new AuctionSearch(auctionNames)))

      auctionSearch ! Action.AuctionSearch.Register(auction1.ref)
      auctionSearch ! Action.AuctionSearch.Search("audi")

      val expected = List(auction1.ref)
      expectMsg(Action.AuctionSearch.SearchResult(expected))
    }

    "returns empty list when nothing registered" in {
      val auctionSearch = system.actorOf(Props(new AuctionSearch(auctionNames)))

      auctionSearch ! Action.AuctionSearch.Search("")

      val expected = List()
      expectMsg(Action.AuctionSearch.SearchResult(expected))
    }

    "register auctions and matches them properly" in {
      val auction1 = TestProbe("AudiA6dieselmanual")
      val auction2 = TestProbe("Peugeot307gasmanual")

      val auctionSearch = system.actorOf(Props(new AuctionSearch(auctionNames)))

      auctionSearch ! Action.AuctionSearch.Register(auction1.ref)
      auctionSearch ! Action.AuctionSearch.Register(auction2.ref)

      auctionSearch ! Action.AuctionSearch.Search("manual")

      val expected = List(auction1.ref, auction2.ref)
      expectMsg(Action.AuctionSearch.SearchResult(expected))
    }

  }


}

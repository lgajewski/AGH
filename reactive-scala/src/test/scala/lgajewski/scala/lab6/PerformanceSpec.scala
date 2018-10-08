package lgajewski.scala.lab6

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

import scala.concurrent.duration._

class PerformanceSpec extends TestKit(ActorSystem("PerformanceSpec"))
  with WordSpecLike with BeforeAndAfterAll with ImplicitSender {

  var auctionNames: List[String] = null
  var searchQueries: List[String] = null

  val AUCTIONS = 30000
  val SEARCHES = 8000
  val TIMEOUT = 60 seconds

  override def beforeAll(): Unit = {
    auctionNames = (for (i <- 1 to AUCTIONS) yield s"auction #${i % 100}").toList
    searchQueries = (for (i <- 1 to SEARCHES) yield s"auction #${(i * 6) % 110}").toList
  }

  override def afterAll(): Unit = {
    system.terminate
  }

  "Measure a performance" must {

    "of Auction System" in {
      val masterSearch = system.actorOf(Props[MasterSearch], "MasterSearch")

      val parent = TestProbe()
      val seller = parent.childActorOf(Props(new Seller(auctionNames)))

      // variable for time calculation
      var time: Long = 0

      println("Step1: start")
      time = System.currentTimeMillis()
      seller ! Action.Seller.CreateAuctions


      parent.expectMsgPF(TIMEOUT) {
        case Action.Done =>
          println(s"Step1: end, duration: ${System.currentTimeMillis - time} ms")

          println("Step2: start")
          time = System.currentTimeMillis()
          searchQueries.foreach(query => parent.send(masterSearch, Action.AuctionSearch.Search(query)))

          parent.receiveN(searchQueries.length, TIMEOUT)
          println(s"Step2: end, duration: ${System.currentTimeMillis - time} ms")
      }
    }

  }

}

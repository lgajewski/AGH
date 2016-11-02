package lgajewski.scala.lab3

import akka.actor.ActorRef

object Action {

  case object Auction {
    case object Start

    case object Relist

    case class Sold(buyer: ActorRef, seller: ActorRef, bid: BigInt) {
      require(bid > 0)
    }

    case class Bid(who: ActorRef, value: BigInt) {
      require(value > 0)
    }

    case class BidFailed(current: BigInt) {
      require(current > 0)
    }

    case object BidTimerExpired

    case object DeleteTimerExpired
  }

  object Buyer {

    case class Init(balance: BigInt) {
      require(balance > 0)
    }

    case class Bid(auction: ActorRef, value: BigInt) {
      require(value > 0)
    }

    case class StartAuction(auction: ActorRef)

  }

  object Seller {
    case object CreateAuctions
  }

  object ActionSearch {
    case class Register(who: ActorRef)
    case class Search(name: String)
    case class SearchResult(auctions: List[ActorRef])
  }

}
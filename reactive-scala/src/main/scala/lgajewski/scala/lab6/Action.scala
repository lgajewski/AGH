package lgajewski.scala.lab6

import akka.actor.ActorRef

object Action {

  case object Done

  case object Auction {

    case object Start

    case object Relist

    case class Sold(auction: Auction, buyer: ActorRef, seller: ActorRef, bid: BigInt) {
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

    case class Bid(auction: ActorRef, value: BigInt) {
      require(value > 0)
    }

    case class StartAuction(name: String) {
      require(!name.isEmpty)
    }

  }

  object Seller {

    case object CreateAuctions

  }

  object AuctionSearch {

    case class Register(who: Auction)

    case class Search(name: String)

    case class SearchResult(auctions: List[Auction])

  }

  object Notifier {

    case object Done

    case class Notify(title: String, buyer: ActorRef, bid: BigInt) {
      require(bid >= 0)
    }

  }

}
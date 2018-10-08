package lgajewski.scala.lab6

import akka.actor.{Actor, ActorLogging, Props}
import akka.routing._

class MasterSearch extends Actor with ActorLogging {

  val nbOfroutees: Int = 4

  val routees = Vector.fill(nbOfroutees) {
    val r = context.actorOf(Props[AuctionSearch])
    context watch r
    ActorRefRoutee(r)
  }

  var broadcastRouter = {
    Router(BroadcastRoutingLogic(), routees)
  }

  var roundRobicRouter = {
    Router(RoundRobinRoutingLogic(), routees)
  }

  override def receive: Receive = {
    case Action.AuctionSearch.Register(who) =>
      broadcastRouter.route(Action.AuctionSearch.Register(who), sender)
    case Action.AuctionSearch.Search(name) =>
      roundRobicRouter.route(Action.AuctionSearch.Search(name), sender)
  }
}

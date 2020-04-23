package shoppingcart

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import io.grpc.ManagedChannelBuilder
import com.github.phisgr.gatling.grpc.Predef._
import com.example.shoppingcart.shoppingcart._
import scala.util.Try
import scala.util.Random

object RandomUsers {

  val randomUserFeeder = new Iterator[Map[String,String]] {

    private val RNG = new Random

    override def hasNext = true

    override def next: Map[String, String] = {
      val id = s"gatling-${RNG.nextLong.toString}"

      Map("userId" -> id)
    }
  }
}

class ShoppingCartSimulation1 extends Simulation {

  val host = scala.util.Properties.envOrElse("GATLING_HOST", "localhost")
  val portString = scala.util.Properties.envOrElse("GATLING_PORT", "9000")
  val port = Try(portString.toInt).getOrElse(9000)

  val grpcConf = grpc(ManagedChannelBuilder.forAddress(host, port).usePlaintext())

  val userFeeder = RandomUsers.randomUserFeeder
  val itemFeeder = csv("items.csv").eager.random

  // TODO items come from CSV
  // TODO parameter for number of users and other metrics
  // TODO user should add 10 items, get the basket, remove 10 items, repeat N times

  val scn = scenario("Adding items")
    .feed(userFeeder)
    .feed(itemFeeder)
    .exec(
      grpc("Success")
      .rpc(ShoppingCartGrpc.METHOD_ADD_ITEM)
      .payload(session =>
        for (
          userId <- session("userId").validate[String];
          productId <- session("product_id").validate[String];
          productName <- session("product_name").validate[String]
        ) yield AddLineItem(userId, productId = productId, name = productName, quantity = 1)))
    //.extract(a => a)
    //.header(TokenHeaderKey)($("token"))
    //.extract(_.data.split(' ').headOption)(_ saveAs "s")

  setUp(scn.inject(atOnceUsers(5)).protocols(grpcConf))

  //    setUp(scn.inject(rampUsers(200) over (30 seconds)).protocols(httpConf))
}

package shoppingcart

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import io.grpc.ManagedChannelBuilder
import com.github.phisgr.gatling.grpc.Predef._
import com.example.shoppingcart.shoppingcart._

// https://github.com/phiSgr/gatling-grpc
// https://medium.com/@georgeleung_7777/a-demo-of-gatling-grpc-bc92158ca808

class ShoppingCartSimulation1 extends Simulation {

  // TODO the server and port need to be in config
  val grpcConf = grpc(ManagedChannelBuilder.forAddress("localhost", 9000).usePlaintext())

  // val httpProtocol = http
  //   .baseUrl("http://computer-database.gatling.io") // Here is the root for all relative URLs
  //   .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers
  //   .acceptEncodingHeader("gzip, deflate")
  //   .acceptLanguageHeader("en-US,en;q=0.5")
  //   .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val userId = "justinhj1"

  val scn = scenario("Adding items") // A scenario is a chain of requests and pauses
    .exec(grpc("Success")
      .rpc(ShoppingCartGrpc.METHOD_ADD_ITEM)
      .payload(AddLineItem(userId, productId = "product 1", name = "amazing sun glasses 1", quantity = 1)))
    //.header(TokenHeaderKey)($("token"))
    //.extract(_.data.split(' ').headOption)(_ saveAs "s")

  setUp(scn.inject(atOnceUsers(5)).protocols(grpcConf))
}

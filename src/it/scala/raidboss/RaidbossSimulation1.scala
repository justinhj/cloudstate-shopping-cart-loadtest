package raidboss

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import io.grpc.ManagedChannelBuilder
import com.github.phisgr.gatling.grpc.Predef._
import org.justinhj.raidbossservice.raidbossservice._
import scala.util.Try
import scala.util.Random
import io.gatling.commons.validation.Validation
import io.gatling.commons.validation._

object RandomPlayers {

  val randomPlayerFeeder = new Iterator[Map[String,String]] {

    private val RNG = new Random

    override def hasNext = true

    override def next: Map[String, String] = {
      val id = s"player-${RNG.nextLong.toString}"

      Map("playerId" -> id)
    }
  }
}

class RaidBossSimulation1 extends Simulation {

  val host = scala.util.Properties.envOrElse("GATLING_HOST", "localhost")
  val portString = scala.util.Properties.envOrElse("GATLING_PORT", "9000")
  val port = Try(portString.toInt).getOrElse(9000)
  val numUsersString = scala.util.Properties.envOrElse("GATLING_NUM_USERS", "1")
  val numUsers = Try(numUsersString.toInt).getOrElse(1)

  val grpcConf = grpc(ManagedChannelBuilder.forAddress(host, port).usePlaintext())
  val userFeeder = RandomPlayers.randomPlayerFeeder

  val bossDefId = "Angry-Dog-1"
  val groupId = "Villagers-1"

  val instanceId = bossDefId + "-" + groupId + "-" + System.currentTimeMillis.toString

  val scn = scenario("Create and attack")
    .exec(
      grpc("Create Boss")
        .rpc(RaidBossServiceGrpc.METHOD_CREATE_RAID_BOSS)
        .payload(RaidBossCreate(instanceId, bossDefId, groupId)))
    .repeat(10) {
        feed(userFeeder)
        .exec(
          grpc("Attack Boss")
            .rpc(RaidBossServiceGrpc.METHOD_ATTACK_RAID_BOSS)
            .payload(session =>
              for (
                playerId <- session("playerId").validate[String]
              ) yield RaidBossAttack(instanceId, playerId, 10))
              .extract(rbi => Some(rbi.health))(h => h.saveAs("rbiHealth"))
          )
    }
    .exec(
      grpc("View Boss")
        .rpc(RaidBossServiceGrpc.METHOD_VIEW_RAID_BOSS)
        .payload(RaidBossView(instanceId))
        .extract(rbi => Some(rbi))(rbi => rbi.saveAs("finalBoss"))
    )
    .exec(session => {
      println(s"Final view ${session("finalBoss").as[RaidBossInstance]}")
      session
    })

  setUp(scn.inject(rampUsers(numUsers) during (30 seconds)).protocols(grpcConf))
}

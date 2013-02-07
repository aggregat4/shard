package shard.server

import org.slf4j.LoggerFactory
import akka.config.Supervision._
import akka.actor.{Supervisor, Actor}
import cc.spray.{SprayCanRootService, HttpService}
import cc.spray.can.HttpServer

object SprayServer extends App {

  LoggerFactory.getLogger(getClass) // initialize SLF4J early

  val mainModule = new ShardService {
    // bake your module cake here
  }

  val httpService = Actor.actorOf(new HttpService(mainModule.shardService))
  val rootService = Actor.actorOf(new SprayCanRootService(httpService))
  val sprayCanServer = Actor.actorOf(new HttpServer())

  Supervisor(
    SupervisorConfig(
      OneForOneStrategy(List(classOf[Exception]), 3, 100),
      List(
        Supervise(httpService, Permanent),
        Supervise(rootService, Permanent),
        Supervise(sprayCanServer, Permanent)
      )
    )
  )
}
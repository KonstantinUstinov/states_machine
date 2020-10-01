package com.states.machine

import akka.actor.ActorSystem
import akka.event.Logging
import com.states.machine.utilities.ConfigProvider

object Boot extends App with ConfigProvider {

  implicit val system: ActorSystem = ActorSystem("state-machine")
  implicit val executor = system.dispatcher
  implicit val logger = Logging(system, getClass)

  ActorRegistry.init(system)

  val server = new RoutingServer()
  server.startServer(config.getString("server.host"), config.getInt("server.port"))

}

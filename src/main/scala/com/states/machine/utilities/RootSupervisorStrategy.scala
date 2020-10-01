package com.states.machine.utilities

import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.actor.{ActorInitializationException, ActorKilledException, DeathPactException, OneForOneStrategy, SupervisorStrategy, SupervisorStrategyConfigurator}
import com.typesafe.scalalogging.LazyLogging

class RootSupervisorStrategy extends SupervisorStrategyConfigurator with LazyLogging {
  override def create(): SupervisorStrategy = {

    OneForOneStrategy()({
      case _: ActorInitializationException ⇒ Stop
      case _: ActorKilledException ⇒ Stop
      case _: DeathPactException ⇒ Stop
      case e: Exception ⇒
        logger.error("Root level actor crashed, will be restarted", e)
        Restart
    })
  }

}

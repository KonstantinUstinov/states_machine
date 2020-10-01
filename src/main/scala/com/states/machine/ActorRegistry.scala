package com.states.machine

import akka.actor.{ActorRef, ActorRefFactory, ActorSystem, Props}
import akka.util.Timeout
import com.states.machine.controler.{MatrixActor, StateActor}

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.language.postfixOps

object ActorRegistry {

  implicit val timeout = Timeout(15.seconds)

  def init(system: ActorSystem): Unit = {

    val refMatrix = classOf[MatrixActor]
    system.actorOf(Props(refMatrix), refMatrix.getName)

    val refState = classOf[StateActor]
    system.actorOf(Props(refState), refState.getName)

  }

  def getActor(ref: Class[_], context: ActorRefFactory): ActorRef = {
    Await.result(context.actorSelection(s"/user/${ref.getName}").resolveOne(), 3 seconds)
  }

}

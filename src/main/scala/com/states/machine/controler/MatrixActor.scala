package com.states.machine.controler

import akka.actor.{Actor, ActorLogging}
import com.states.machine.model.Items.FlowState
import com.states.machine.model.MatrixStorage

case class GetMatrix()
case class AddMatrix(flow: FlowState)

class MatrixActor extends Actor with ActorLogging with MatrixStorage {
  override def receive: Receive = {
    case GetMatrix() =>
      sender ! getMatrix()
    case AddMatrix(flow) =>
      sender ! addMatrix(flow)
    case _ => log.error("Not Support")
  }
}

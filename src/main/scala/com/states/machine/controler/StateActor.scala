package com.states.machine.controler

import akka.actor.{Actor, ActorLogging}
import com.states.machine.model.Items.FlowState
import com.states.machine.model.StateStorage

case class AddNewFSM(flow: FlowState)
case class ChangeState(id: Int, state: String)

class StateActor extends Actor with ActorLogging with StateStorage {
  override def receive: Receive = {
    case AddNewFSM(flow) =>
      sender ! addFSM(flow)
    case  ChangeState(id, state)  =>
      sender ! updateState(id, state)
    case _ => log.error("Not Support")
  }

}

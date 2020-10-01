package com.states.machine.model

import com.states.machine.fsm.SideEffect

object Items {

  sealed trait StateM extends SideEffect
  object Init extends StateM
  object Pending extends StateM
  object Closed extends StateM
  object Finished extends StateM

  sealed trait EventM
  object OnInit extends EventM
  object OnPending extends EventM
  object OnClosed extends EventM
  object OnFinished extends EventM

  case class FlowState(initState: String, graph: List[GraphState])
  case class GraphState(state: String, list: List[String])

  case class ErrorDetail(code: Int, error: String, message: Option[String], info: Option[String])
  case class InfoDetail(id: Int, msg: String)
  case class NewState(state: String)
  case class ChangeStateResponse(id: Int, state: String, newState: String)

  def stringToState(state: String): StateM = {
    state match {
      case "Init"     => Init
      case "Pending"  => Pending
      case "Closed"   => Closed
      case "Finished" => Finished
      case _ => throw new  Exception("Wrong State Format")
    }
  }

  def stringToStateEvent(state: String): (EventM, StateM) = {
    val event = state match {
      case "Init"     => OnInit
      case "Pending"  => OnPending
      case "Closed"   => OnClosed
      case "Finished" => OnFinished
      case _ => throw new  Exception("Wrong State Format")
    }
    (event, stringToState(state))
  }

  def StateToString(state: StateM): String = {
    state match {
      case Init => "Init"
      case Pending => "Pending"
      case Closed => "Closed"
      case Finished => "Finished"
      case _ => throw new  Exception("Wrong State Format")
    }
  }

  def convertFlow(flow: FlowState) : (StateM, Map[StateM, Map[EventM, StateM]]) = {
    (stringToState(flow.initState), flow.graph.map(p => stringToState(p.state) -> p.list.map(stringToStateEvent).toMap).toMap)
  }
}

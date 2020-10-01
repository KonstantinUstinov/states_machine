package com.states.machine.model

import com.states.machine.fsm.FSM
import com.states.machine.model.Items.{EventM, FlowState, StateM}

import scala.util.{Failure, Success, Try}

trait StateStorage {
  private val storage = scala.collection.mutable.HashSet[(Int, FSM[StateM, EventM])]()

  def addFSM(flow: FlowState): Try[Int] = {
    Try(Items.convertFlow(flow))
      .map(flow => FSM.fromMap[StateM, EventM](flow._1, flow._2))
      .map{fsm =>
        val maxId = storage.size + 1
        storage.add((maxId, fsm))
        maxId
      }
  }

  def updateState(id: Int, state: String): Try[(String, String, Int)] = {
    getFSM(id).map{fsm => Try{
      val currentSate = Items.StateToString(fsm.state())
      val newState = Items.StateToString(fsm.transition(Items.stringToStateEvent(state)._1))
      (currentSate, newState, id) }
    } match {
      case Some(result) => result
      case None => Failure(new Exception("FSM not found"))
    }
  }

  def getFSM(id: Int): Option[FSM[StateM, EventM]] = {
    storage.find(_._1 == id).map(_._2)
  }
}

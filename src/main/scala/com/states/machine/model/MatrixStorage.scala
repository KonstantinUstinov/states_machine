package com.states.machine.model

import com.states.machine.model.Items.{FlowState, GraphState}

import scala.util.Try

trait MatrixStorage {
  private val matrix = scala.collection.mutable.HashSet[FlowState](FlowState("Init", List(GraphState("Init", List("Pending", "Finished")), GraphState("Finished", List("Closed")), GraphState("Pending", List("Closed")))))

  def addMatrix(flow: FlowState) = {
    Try(Items.convertFlow(flow)).map { _ =>
      matrix.clear()
      matrix.add(flow)
    }
  }

  def getMatrix() = matrix.head

}

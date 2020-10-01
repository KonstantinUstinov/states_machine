package com.states.machine.fsm

import com.states.machine.model.Items
import com.states.machine.model.Items._
import org.scalatest.{FlatSpec, Matchers}

class fsmTest extends FlatSpec with Matchers  {

  it should "work FSM properly" in {

    val fsm = FSM[StateM, EventM](Init,
      State(Init, On(OnPending, Pending), On(OnFinished, Finished)),
      State(Finished, On(OnClosed, Closed)),
      State(Pending, On(OnClosed, Closed))
    )
    fsm.state() shouldBe Init

    fsm.transition(OnPending) shouldBe Pending
    fsm.state() shouldBe Pending

    fsm.transition(OnClosed) shouldBe Closed
    fsm.state() shouldBe Closed

    val fsm_copy = fsm.copy()
    fsm_copy.state() shouldBe Closed
  }

  it should "work FSM properly from Flow" in {
    val flow = FlowState("Init", List(GraphState("Init", List("Pending", "Finished")), GraphState("Finished", List("Closed")), GraphState("Pending", List("Closed"))))
    val result = Items.convertFlow(flow)

    val fsm = FSM.fromMap[StateM, EventM](result._1, result._2)

    fsm.state() shouldBe Init

    fsm.transition(OnPending) shouldBe Pending
    fsm.state() shouldBe Pending

    fsm.transition(OnClosed) shouldBe Closed
    fsm.state() shouldBe Closed

    val fsm_copy = fsm.copy()
    fsm_copy.state() shouldBe Closed
  }

}

package com.states.machine.fsm

import scala.collection.mutable
import scala.language.postfixOps
import scala.util.control.NonFatal

case class InvalidFSMTransition(msg: String, e: Throwable) extends Exception(msg, e)


class FSM[S <: SideEffect, E] private[fsm](private var currentState: S,
                                           private val graph: Map[S, Map[E, S]]) {

  @throws(classOf[InvalidFSMTransition])
  def transition(event: E): S = this.synchronized {
    currentState.onExit()
    try {
      currentState = graph(currentState)(event)
    } catch {
      case NonFatal(e) =>
        throw InvalidFSMTransition(s"Failed to transition from $currentState for $event", e)
    }
    currentState.onEnter()
    currentState
  }

  def state(): S = this.synchronized {
    currentState
  }

  def copy(): FSM[S, E] = {
    new FSM(currentState, graph)
  }
}

object FSM {

  def apply[S <: SideEffect, E](initial: S, states: State[S, E]*): FSM[S, E] = {
    val builder = new GraphBuilder[S, E].initialState(initial)
    states foreach builder.setState
    builder.build()
  }

  def fromMap[S <: SideEffect, E](initial: S, graph: Map[S, Map[E, S]]): FSM[S, E] = {
    new FSM(initial, graph)
  }
}

private[fsm] class GraphBuilder[S <: SideEffect, E] {
  self =>

  private var initial: S = _
  private val graph: mutable.Map[S, Map[E, S]] = mutable.Map()

  def initialState(state: S): GraphBuilder[S, E] = {
    initial = state
    self
  }

  def setState(state: State[S, E]): GraphBuilder[S, E] = {
    graph.put(state.state, state.trans.toMap)
    self
  }

  def build(): FSM[S, E] = {
    new FSM(initial, graph.toMap)
  }
}

case class State[S <: SideEffect, E](state: S, transitions: On[S, E]*) {
  val trans: mutable.Map[E, S] = mutable.Map()
  transitions.foreach(on => trans.put(on.event, on.dest))
}

case class On[+S <: SideEffect, +E](event: E, dest: S)

trait SideEffect {
  def onEnter(): Unit = {}
  def onExit(): Unit = {}
}

package com.states.machine.rest

import akka.http.scaladsl.server.RouteConcatenation._

trait Service extends MatrixService with StateService {
  def routes = matrixRoute ~ stateRoute
}

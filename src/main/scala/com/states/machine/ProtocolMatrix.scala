package com.states.machine

import com.states.machine.model.Items.{ErrorDetail, FlowState, GraphState}
import spray.json.DefaultJsonProtocol

trait ProtocolMatrix  extends DefaultJsonProtocol {
  implicit val graphStateFormat = jsonFormat2(GraphState.apply)
  implicit val flowStateFormat = jsonFormat2(FlowState.apply)
  implicit val errorDetailFormat = jsonFormat4(ErrorDetail.apply)
}

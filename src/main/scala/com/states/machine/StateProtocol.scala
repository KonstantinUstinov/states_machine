package com.states.machine

import com.states.machine.controler.ChangeState
import com.states.machine.model.Items.{ChangeStateResponse, ErrorDetail, InfoDetail, NewState}
import spray.json.DefaultJsonProtocol

trait StateProtocol extends DefaultJsonProtocol {
  implicit val errorFormat = jsonFormat4(ErrorDetail.apply)
  implicit val infoDetailFormat = jsonFormat2(InfoDetail.apply)
  implicit val newStateFormat = jsonFormat1(NewState.apply)
  implicit val changeStateFormat = jsonFormat3(ChangeStateResponse.apply)
}

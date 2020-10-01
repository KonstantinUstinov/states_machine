package com.states.machine.rest

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.states.machine.{ActorRegistry, StateProtocol}
import com.states.machine.controler.{AddNewFSM, ChangeState, GetMatrix, MatrixActor, StateActor}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContextExecutor
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.states.machine.model.Items.{ChangeStateResponse, ErrorDetail, FlowState, InfoDetail, NewState}

import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

trait StateService extends StateProtocol with  LazyLogging {

  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  implicit val stateTimeout = Timeout(15.seconds)

  lazy val matrixActor = ActorRegistry.getActor(classOf[MatrixActor], system)
  lazy val stateActor = ActorRegistry.getActor(classOf[StateActor], system)

  val stateRoute = pathPrefix("state") {
    pathPrefix("add") {
      post {
        onComplete(matrixActor ? GetMatrix()) {
          case Success(response: FlowState) =>
            onComplete(stateActor ? AddNewFSM(response)) { item =>
              (futureState orElse futureDefault)(item)
            }
        }
      }
    } ~ put {
      pathPrefix("change" / IntNumber) { id =>
        entity(as[NewState]) { item =>
          onComplete(stateActor ? ChangeState(id, item.state)) { result =>
            (futureChangeState orElse futureDefault)(result)
          }
        }
      }
    }
  }

  val futureChangeState : PartialFunction[Try[Any], server.Route] = {
    case Success(response: Try[(String, String, Int)]) =>
      response match {
        case Success(value) =>
          complete(200, ChangeStateResponse(value._3, value._2, value._1))
        case Failure(e) =>
          complete(404, ErrorDetail(404, e.toString, Some(e.getMessage), Some(e.getLocalizedMessage)))
      }
  }

  val futureState : PartialFunction[Try[Any], server.Route] = {
    case Success(response: Try[Int]) =>
      logger.debug("Success Try")
      response match {
        case Success(value) =>
          complete(200, InfoDetail(value, "Тew FSM added successfully"))
        case Failure(e) =>
          complete(500, ErrorDetail(500, e.toString, Some(e.getMessage), Some(e.getLocalizedMessage)))
      }
  }

  val futureDefault: PartialFunction[Try[Any], server.Route] = {
    case Success(response: ErrorDetail)       =>
      logger.debug("Success ErrorDetail")
      complete(response.code, response)

    case Failure(e: Exception)                =>
      logger.error(e.getMessage)
      complete(500, ErrorDetail(e.hashCode(), e.toString, Some(e.getMessage), Some(e.getLocalizedMessage)))

    case unknown: Any                         =>
      logger.error("unknown.toString")
      complete(500, unknown.toString)
  }


}

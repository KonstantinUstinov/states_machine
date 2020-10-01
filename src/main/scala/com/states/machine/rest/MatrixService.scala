package com.states.machine.rest

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives.pathPrefix
import akka.stream.Materializer
import scala.concurrent.ExecutionContextExecutor
import akka.util.Timeout
import com.states.machine.{ActorRegistry, ProtocolMatrix}
import com.states.machine.controler.{AddMatrix, GetMatrix, MatrixActor}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import com.states.machine.model.Items.{ErrorDetail, FlowState}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

trait MatrixService extends  ProtocolMatrix with  LazyLogging {

  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  implicit val matrixTimeout = Timeout(15.seconds)

  lazy val matrixManager = ActorRegistry.getActor(classOf[MatrixActor], system)

  val matrixRoute = pathPrefix("matrix") {
    get {
      onComplete(matrixManager ? GetMatrix()) { item =>
        (futureMatrixHandler orElse futureHandler) (item)
      }
    } ~ post {
      entity(as[FlowState]) { item =>
        onComplete(matrixManager ? AddMatrix(item)) { item =>
          (futureMatrixHandler  orElse futureHandler)(item)
        }
      }
    }
  }

  val futureMatrixHandler: PartialFunction[Try[Any], server.Route] = {
    case Success(response: FlowState)             =>
      logger.debug("Success FlowState")
      complete(200, response)

    case Success(response: Try[Boolean])       =>
      logger.debug("Success Try")
      response match {
        case Success(_) =>
          complete(200, "Success add flow")
        case Failure(e) =>
          complete(500, ErrorDetail(500, e.toString, Some(e.getMessage), Some(e.getLocalizedMessage)))
      }
  }

  val futureHandler: PartialFunction[Try[Any], server.Route] = {
    case Success(resp: ErrorDetail)           =>
      logger.debug("Success ErrorDetail")
      complete(resp.code, resp)

    case Failure(e: Exception)                =>
      logger.error(e.getMessage)
      complete(500, ErrorDetail(e.hashCode(), e.toString, Some(e.getMessage), Some(e.getLocalizedMessage)))

    case unknown: Any                         =>
      logger.error("unknown.toString")
      complete(500, unknown.toString)
  }

}

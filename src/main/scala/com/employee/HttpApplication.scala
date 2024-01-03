package com.employee

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import com.employee.infrastructure.endpoints.{CreateEmployeeEndpoint, DeleteEmployeeEndpoint, GetAllEmployeesEndpoint, GetEmployeeEndpoint, UpdateEmployeeEndpoint}
import com.employee.infrastructure.interfaces.EmployeeRepositoryImplementation
import akka.http.scaladsl.server.RouteConcatenation._
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object HttpApplication extends App {

  protected val config: Config = ConfigFactory.load("application.conf")
  implicit val as: ActorSystem = ActorSystem()
  private val logger           = LoggerFactory.getLogger(getClass)
  val repository               = new EmployeeRepositoryImplementation
  val serviceConfig = config.getConfig("service")

  val serviceInterface: Route =
    AkkaHttpServerInterpreter()(as.dispatcher).toRoute(new CreateEmployeeEndpoint(repository).endpointToUse) ~
    AkkaHttpServerInterpreter()(as.dispatcher).toRoute(new UpdateEmployeeEndpoint(repository).endpointToUse) ~
    AkkaHttpServerInterpreter()(as.dispatcher).toRoute(new GetEmployeeEndpoint(repository).endpointToUse) ~
    AkkaHttpServerInterpreter()(as.dispatcher).toRoute(new GetAllEmployeesEndpoint(repository).endpointToUse) ~
    AkkaHttpServerInterpreter()(as.dispatcher).toRoute(new DeleteEmployeeEndpoint(repository).endpointToUse)

  logger.info("Starting application")

  val bindingFuture = Http().newServerAt(serviceConfig.getString("host"), serviceConfig.getInt("port")).bindFlow(serviceInterface)
  logger.info("Finish application")
  bindingFuture.onComplete {
    case Success(binding) =>
      logger.info(s"Success binding")
      val address = binding.localAddress
      logger.info(s"Server online at http://${address.getHostString}:${address.getPort}/")
    case Failure(exception) =>
      logger.error("Server binding failed", exception)
      sys.exit()
  }

}


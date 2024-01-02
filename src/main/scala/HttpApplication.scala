import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.RouteConcatenation._
import com.typesafe.config.{Config, ConfigFactory}
import infrastructure.endpoints.{CreateEmployeeEndpoint, DeleteEmployeeEndpoint, GetEmployeeEndpoint, UpdateEmployeeEndpoint}
import infrastructure.interfaces.EmployeeRepositoryImplementation
import org.slf4j.LoggerFactory
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

import scala.concurrent.ExecutionContext.Implicits.global

object HttpApplication extends App {

  protected val config: Config = ConfigFactory.load("application.conf")
  implicit val as: ActorSystem = ActorSystem()
  private val logger           = LoggerFactory.getLogger(getClass)
  val repository               = new EmployeeRepositoryImplementation

  val serviceInterface: Route =
    AkkaHttpServerInterpreter()(as.dispatcher).toRoute(new CreateEmployeeEndpoint(repository).endpointToUse) ~
    AkkaHttpServerInterpreter()(as.dispatcher).toRoute(new UpdateEmployeeEndpoint(repository).endpointToUse) ~
    AkkaHttpServerInterpreter()(as.dispatcher).toRoute(new GetEmployeeEndpoint(repository).endpointToUse) ~
    AkkaHttpServerInterpreter()(as.dispatcher).toRoute(new DeleteEmployeeEndpoint(repository).endpointToUse)

  logger.info("Starting application")

  Http().newServerAt("localhost", 8080).bindFlow(serviceInterface)
}

package infrastructure.endpoints

import com.domain.Entities.Employee
import com.domain.interfaces.EmployeeRepository
import com.domain.useCases.DeleteEmployeeUseCase
import infrastructure.dto.client.ErrorResponse
import infrastructure.dto.client.ErrorResponseJsonFormat.errorResponseJsonFormat
import infrastructure.dto.client.deleteEmployee.ServiceRequestJsonFormatter.serviceRequestDeleteEmployeeJF
import infrastructure.dto.client.deleteEmployee.ServiceResponseJsonFormatter.serviceResponseDeleteEmployeeJF
import infrastructure.dto.client.deleteEmployee.{ServiceRequest, ServiceResponse}
import infrastructure.dto.db.PostgresResponse
import sttp.model.StatusCode
import sttp.model.headers.WWWAuthenticateChallenge
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.spray.jsonBody
import sttp.tapir.model.UsernamePassword
import sttp.tapir.{auth, endpoint, statusCode, Endpoint}

import scala.concurrent.{ExecutionContext, Future}

class DeleteEmployeeEndpoint(repository: EmployeeRepository[PostgresResponse, Employee]) {
  private def jsonBodyRequest = jsonBody[ServiceRequest].description("Delete Record Request") //.example()

  private def jsonBodyResponse = jsonBody[ServiceResponse].description("Delete Record Response") //.example()

  private def jsonBodyError = jsonBody[ErrorResponse].description("Delete Record Error") //.example()

  val endpointDefinition: Endpoint[UsernamePassword, ServiceRequest, ErrorResponse, ServiceResponse, Any] =
    endpoint.put
      .in("delete-employee")
      .in(jsonBodyRequest)
      .errorOut(jsonBodyError)
      .out(jsonBodyResponse)
      .out(statusCode(StatusCode.Ok).description("Successful response"))
      .description("Endpoint used to delete an employee in the DB using PUT")
      .name("delete-employee")
      .securityIn(auth.basic[UsernamePassword](WWWAuthenticateChallenge("basic")))

  private val securityLogic: UsernamePassword => Future[Either[ErrorResponse, String]] = { credentials =>
    if (credentials.password.contains("password")) {
      Future.successful(Right(credentials.username))
    } else {
      Future.successful(Left(ErrorResponse(401, "Provided credentials are incorrect")))
    }
  }

  private def serverLogicCorrectCredentials(input: ServiceRequest)(implicit ec: ExecutionContext) = {
    try {
      val restrievedResult: Future[Boolean] = DeleteEmployeeUseCase(repository).deleteEmployee(
        id = input.id.map(_.toString),
        email = input.email
      )
      restrievedResult.flatMap { result =>
        Future.successful(
          Right(
            ServiceResponse(
              if (result) "deleted" else "not_deleted"
            )
          )
        )
      }
    } catch {
      case e: Exception => Future.successful(Left(ErrorResponse(500, e.getMessage)))
    }
  }

  private val serverLogic: Either[ErrorResponse, String] => ServiceRequest => Future[Either[ErrorResponse, ServiceResponse]] = { check => in =>
    implicit val ec: ExecutionContext = repository.ec

    check match {
      case Right(_)    => serverLogicCorrectCredentials(in)
      case Left(value) => Future.successful(Left(ErrorResponse(value.code, value.message)))
    }

  }

  val endpointToUse =
    endpointDefinition
      .serverSecurityLogicSuccess(securityLogic)
      .serverLogic(serverLogic)

}

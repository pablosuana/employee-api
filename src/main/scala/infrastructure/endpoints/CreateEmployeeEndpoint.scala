package infrastructure.endpoints

import com.domain.Entities.Employee
import com.domain.interfaces.EmployeeRepository
import com.domain.useCases.CreateEmployeeUseCase
import infrastructure.dto.client.ErrorResponse
import infrastructure.dto.client.ErrorResponseJsonFormat.errorResponseJsonFormat
import infrastructure.dto.client.createEmployee.ServiceRequestJsonFormatter.serviceRequestJF
import infrastructure.dto.client.createEmployee.ServiceResponseJsonFormatter.serviceResponseJF
import infrastructure.dto.client.createEmployee.{ServiceRequest, ServiceResponse}
import infrastructure.dto.db.PostgresResponse
import sttp.model.StatusCode
import sttp.model.headers.WWWAuthenticateChallenge
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.spray.jsonBody
import sttp.tapir.model.UsernamePassword
import sttp.tapir.{Endpoint, auth, endpoint, statusCode}

import java.sql.Timestamp
import java.util.Date
import scala.concurrent.{ExecutionContext, Future}

// This should be instantiated from an interface as well, but I have not more time

class CreateEmployeeEndpoint(repository: EmployeeRepository[PostgresResponse, Employee]) {
  private def jsonBodyRequest  = jsonBody[ServiceRequest].description("Create Record Request")   //.example()
  private def jsonBodyResponse = jsonBody[ServiceResponse].description("Create Record Response") //.example()
  private def jsonBodyError    = jsonBody[ErrorResponse].description("Create Record Error")      //.example()

  val endpointDefinition: Endpoint[UsernamePassword, ServiceRequest, ErrorResponse, ServiceResponse, Any] =
    endpoint.post
      .in("create-employee")
      .in(jsonBodyRequest)
      .errorOut(jsonBodyError)
      .out(jsonBodyResponse)
      .out(statusCode(StatusCode.Ok).description("Successful response"))
      .description("Endpoint used to create a new employee in the DB using POST")
      .name("create-endpoint")
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
      val timestamp = new Timestamp(new Date().getTime)
      val creationStatus = CreateEmployeeUseCase(repository).createEmployee(
        Employee(
          id = input.id,
          email = input.email,
          fullName = input.full_name,
          dateOfBirth = input.date_of_birth,
          hobbies = input.hobbies
        ),
        timestamp.toString
      )
      creationStatus.flatMap { f =>
        val status = if (f) "completed" else "failed"
        Future.successful(Right(ServiceResponse(input.id, input.email, status, timestamp.toString)))
      }
    } catch {
      case e: Exception => Future.successful(Left(ErrorResponse(404, e.getMessage)))
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

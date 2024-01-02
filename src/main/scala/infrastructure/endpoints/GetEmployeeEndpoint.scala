package infrastructure.endpoints

import com.domain.Entities.Employee
import com.domain.interfaces.EmployeeRepository
import com.domain.useCases.GetEmployeeUseCase
import infrastructure.dto.client.ErrorResponse
import infrastructure.dto.client.ErrorResponseJsonFormat.errorResponseJsonFormat
import infrastructure.dto.client.getEmployee.ServiceResponseJsonFormatter.serviceResponseGetEmployeeJF
import infrastructure.dto.client.getEmployee.{Metadata, Result, ServiceResponse}
import infrastructure.dto.db.PostgresResponse
import sttp.model.StatusCode
import sttp.model.headers.WWWAuthenticateChallenge
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.spray.jsonBody
import sttp.tapir.model.UsernamePassword
import sttp.tapir.{Endpoint, auth, endpoint, query, statusCode}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class GetEmployeeEndpoint(repository: EmployeeRepository[PostgresResponse, Employee]) {

  private def jsonBodyResponse = jsonBody[ServiceResponse].description("Get Record Response") //.example()

  private def jsonBodyError = jsonBody[ErrorResponse].description("Get Record Error") //.example()

  // TODO this should be done as a get endpoint, where parameters are passed in the url

  val endpointDefinition: Endpoint[UsernamePassword, UUID, ErrorResponse, ServiceResponse, Any] =
    endpoint.get
      .in("get-employee")
      .in(query[UUID]("id"))
      .errorOut(jsonBodyError)
      .out(jsonBodyResponse)
      .out(statusCode(StatusCode.Ok).description("Successful response"))
      .description("Endpoint used to get info about an employee in the DB using POST")
      .name("get-endpoint")
      .securityIn(auth.basic[UsernamePassword](WWWAuthenticateChallenge("basic")))

  private val securityLogic: UsernamePassword => Future[Either[ErrorResponse, String]] = { credentials =>
    if (credentials.password.contains("password")) {
      Future.successful(Right(credentials.username))
    } else {
      Future.successful(Left(ErrorResponse(401, "Provided credentials are incorrect")))
    }
  }

  private def serverLogicCorrectCredentials(id: UUID)(implicit ec: ExecutionContext) = {
    try {
      val restrievedResult: Future[Option[PostgresResponse]] =
        GetEmployeeUseCase(repository).getEmployee(id.toString)

      restrievedResult.flatMap { employee =>
        val employeeFound = if (employee.nonEmpty) true else false
        if (employeeFound) {
          Future.successful(
            Right(
              ServiceResponse(
                result = Result(
                  id = UUID.fromString(employee.get.id),
                  email = employee.get.email,
                  full_name = employee.get.full_name,
                  date_of_birth = employee.get.date_of_birth,
                  hobbies = employee.get.hobbies.split(",")
                ),
                metadata = Metadata(
                  updated_at = employee.get.updated_at
                )
              )
            )
          )
        } else Future.successful(Left(ErrorResponse(404, "Employee not found")))
      }
    } catch {
      case e: Exception => Future.successful(Left(ErrorResponse(500, e.getMessage)))
    }
  }

  private val serverLogic: Either[ErrorResponse, String] => UUID => Future[Either[ErrorResponse, ServiceResponse]] = { check => id =>
    implicit val ec: ExecutionContext = repository.ec

    check match {
      case Right(_)    => serverLogicCorrectCredentials(id)
      case Left(value) => Future.successful(Left(ErrorResponse(value.code, value.message)))
    }

  }

  val endpointToUse =
    endpointDefinition
      .serverSecurityLogicSuccess(securityLogic)
      .serverLogic(serverLogic)

}

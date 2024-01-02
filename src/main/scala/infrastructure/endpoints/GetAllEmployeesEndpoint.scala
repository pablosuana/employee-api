package infrastructure.endpoints

import com.domain.Entities.Employee
import com.domain.interfaces.EmployeeRepository
import com.domain.useCases.GetAllEmployeesUseCase
import infrastructure.dto.client.ErrorResponse
import infrastructure.dto.client.ErrorResponseJsonFormat.errorResponseJsonFormat
import infrastructure.dto.client.getAllEmployees.ServiceResponseJsonFormatter.serviceResponseGetEmployeeJF
import infrastructure.dto.client.getAllEmployees.{EmployeeData, Metadata, Result, ServiceResponse}
import infrastructure.dto.db.PostgresResponse
import sttp.model.StatusCode
import sttp.model.headers.WWWAuthenticateChallenge
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.spray.jsonBody
import sttp.tapir.model.UsernamePassword
import sttp.tapir.{Endpoint, auth, endpoint, statusCode}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class GetAllEmployeesEndpoint(repository: EmployeeRepository[PostgresResponse, Employee]) {

  private def jsonBodyResponse = jsonBody[ServiceResponse].description("Get Record Response") //.example()

  private def jsonBodyError = jsonBody[ErrorResponse].description("Get Record Error") //.example()

  val endpointDefinition: Endpoint[UsernamePassword, Unit, ErrorResponse, ServiceResponse, Any] =
    endpoint.get
      .in("get-all-employees")
      .errorOut(jsonBodyError)
      .out(jsonBodyResponse)
      .out(statusCode(StatusCode.Ok).description("Successful response"))
      .description("Endpoint used to get info about all employees in the DB using POST")
      .name("get-all-endpoint")
      .securityIn(auth.basic[UsernamePassword](WWWAuthenticateChallenge("basic")))

  private val securityLogic: UsernamePassword => Future[Either[ErrorResponse, String]] = { credentials =>
    if (credentials.password.contains("password")) {
      Future.successful(Right(credentials.username))
    } else {
      Future.successful(Left(ErrorResponse(401, "Provided credentials are incorrect")))
    }
  }

  private def serverLogicCorrectCredentials(implicit ec: ExecutionContext) = {
    try {
      val retrievedResult: Future[Seq[PostgresResponse]] =
        GetAllEmployeesUseCase(repository).getAllEmployees

      retrievedResult.flatMap { employees =>
        val employeeFound = if (employees.nonEmpty) true else false
        if (employeeFound) {
          Future.successful(
            Right(
              ServiceResponse(
                employees = employees.map(employee =>
                  EmployeeData(
                    result = Result(
                      id = UUID.fromString(employee.id),
                      email = employee.email,
                      full_name = employee.full_name,
                      date_of_birth = employee.date_of_birth,
                      hobbies = employee.hobbies.split(",")
                    ),
                    metadata = Metadata(
                      updated_at = employee.updated_at
                    )
                  )
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

  private val serverLogic: Either[ErrorResponse, String] => Unit => Future[Either[ErrorResponse, ServiceResponse]] = { check => id =>
    implicit val ec: ExecutionContext = repository.ec

    check match {
      case Right(_)    => serverLogicCorrectCredentials
      case Left(value) => Future.successful(Left(ErrorResponse(value.code, value.message)))
    }

  }

  val endpointToUse =
    endpointDefinition
      .serverSecurityLogicSuccess(securityLogic)
      .serverLogic(serverLogic)

}

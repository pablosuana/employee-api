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
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.spray.jsonBody
import sttp.tapir.{Endpoint, endpoint, query, statusCode}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class GetEmployeeEndpoint(repository: EmployeeRepository[PostgresResponse, Employee]) {

  private def jsonBodyResponse = jsonBody[ServiceResponse].description("Get Record Response") //.example()

  private def jsonBodyError = jsonBody[ErrorResponse].description("Get Record Error") //.example()

  val endpointDefinition: Endpoint[Unit, UUID, ErrorResponse, ServiceResponse, Any] =
    endpoint.get
      .in("get-employee")
      .in(query[UUID]("id"))
      .errorOut(jsonBodyError)
      .out(jsonBodyResponse)
      .out(statusCode(StatusCode.Ok).description("Successful response"))
      .description("Endpoint used to get info about an employee in the DB using POST")
      .name("get-endpoint")


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

  private val serverLogic: UUID => Future[Either[ErrorResponse, ServiceResponse]] = { id =>
    implicit val ec: ExecutionContext = repository.ec

    serverLogicCorrectCredentials(id)
  }

  val endpointToUse =
    endpointDefinition
      .serverLogic(serverLogic)

}

package com.employee.infrastructure.endpoints

import com.employee.domain.entities.Employee
import com.employee.domain.interfaces.EmployeeRepository
import com.employee.domain.useCases.GetEmployeeUseCase
import com.employee.infrastructure.dto.client.ErrorResponse
import com.employee.infrastructure.dto.client.ErrorResponseJsonFormat.errorResponseJsonFormat
import com.employee.infrastructure.dto.client.getEmployee.ServiceResponseJsonFormatter.getEmployeeResponseJF
import com.employee.infrastructure.dto.client.getEmployee.{GetEmployeeResponse, Metadata, Result}
import com.employee.infrastructure.dto.db.PostgresResponse
import org.slf4j.LoggerFactory
import sttp.model.StatusCode
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.spray.jsonBody
import sttp.tapir.{Endpoint, endpoint, query, statusCode}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class GetEmployeeEndpoint(repository: EmployeeRepository[PostgresResponse, Employee]) {
  private val logger = LoggerFactory.getLogger(getClass)
  logger.info(s"Initialising GetAllEmployeesEndpoint endpoint")

  private def jsonBodyResponse = jsonBody[GetEmployeeResponse].description("Get Record Response").example(Utils.readJsonExample("examples/getEmployeeSuccessResponse.json").convertTo[GetEmployeeResponse])

  private def jsonBodyError = jsonBody[ErrorResponse].description("Get Record Error").example(Utils.readJsonExample("examples/getEmployeeErrorResponse.json").convertTo[ErrorResponse])

  val endpointDefinition: Endpoint[Unit, UUID, ErrorResponse, GetEmployeeResponse, Any] =
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
              GetEmployeeResponse(
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

  private val serverLogic: UUID => Future[Either[ErrorResponse, GetEmployeeResponse]] = { id =>
    implicit val ec: ExecutionContext = repository.ec

    serverLogicCorrectCredentials(id)
  }

  val endpointToUse =
    endpointDefinition
      .serverLogic(serverLogic)

}

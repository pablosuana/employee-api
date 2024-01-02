package com.employee.infrastructure.endpoints

import com.employee.domain.entities.Employee
import com.employee.domain.interfaces.EmployeeRepository
import com.employee.domain.useCases.GetAllEmployeesUseCase
import com.employee.infrastructure.dto.client.ErrorResponse
import com.employee.infrastructure.dto.client.getAllEmployees.{EmployeeData, Metadata, Result, ServiceResponse}
import com.employee.infrastructure.dto.client.getAllEmployees.ServiceResponseJsonFormatter.serviceResponseGetEmployeeJF
import com.employee.infrastructure.dto.client.ErrorResponseJsonFormat.errorResponseJsonFormat
import com.employee.infrastructure.dto.db.PostgresResponse
import org.slf4j.LoggerFactory
import sttp.model.StatusCode
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.spray.jsonBody
import sttp.tapir.{Endpoint, endpoint, statusCode}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class GetAllEmployeesEndpoint(repository: EmployeeRepository[PostgresResponse, Employee]) {
  private val logger = LoggerFactory.getLogger(getClass)
  logger.info(s"Initialising GetAllEmployeesEndpoint endpoint")

  private def jsonBodyResponse = jsonBody[ServiceResponse].description("Get Record Response") //.example()

  private def jsonBodyError = jsonBody[ErrorResponse].description("Get Record Error") //.example()

  val endpointDefinition: Endpoint[Unit, Unit, ErrorResponse, ServiceResponse, Any] =
    endpoint.get
      .in("get-all-employees")
      .errorOut(jsonBodyError)
      .out(jsonBodyResponse)
      .out(statusCode(StatusCode.Ok).description("Successful response"))
      .description("Endpoint used to get info about all employees in the DB using POST")
      .name("get-all-endpoint")


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

  private val serverLogic: Unit => Future[Either[ErrorResponse, ServiceResponse]] = { id =>
    implicit val ec: ExecutionContext = repository.ec

    serverLogicCorrectCredentials

  }

  val endpointToUse =
    endpointDefinition
      .serverLogic(serverLogic)

}

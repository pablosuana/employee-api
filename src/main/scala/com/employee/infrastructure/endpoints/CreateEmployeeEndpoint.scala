package com.employee.infrastructure.endpoints

import com.employee.domain.entities.Employee
import com.employee.domain.interfaces.EmployeeRepository
import com.employee.domain.useCases.{CreateEmployeeUseCase, EmailAlreadyInDbUseCase}
import com.employee.infrastructure.dto.client.ErrorResponse
import com.employee.infrastructure.dto.client.createEmployee.{CreateEmployeeRequest, CreateEmployeeResponse}
import com.employee.infrastructure.dto.db.PostgresResponse
import org.slf4j.LoggerFactory
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
  private val logger = LoggerFactory.getLogger(getClass)
  logger.info(s"Initialising CreateEmployee endpoint")

  import com.employee.infrastructure.dto.client.createEmployee.RequestJsonFormatter.createEmploymentRequestJF
  import com.employee.infrastructure.dto.client.createEmployee.ResponseJsonFormatter.createaEmployeeResponseJF
  import com.employee.infrastructure.dto.client.ErrorResponseJsonFormat.errorResponseJsonFormat

  private def jsonBodyRequest  = jsonBody[CreateEmployeeRequest].description("Create Record Request").example(Utils.readJsonExample("examples/createEmployeeRequest.json").convertTo[CreateEmployeeRequest])
  private def jsonBodyResponse = jsonBody[CreateEmployeeResponse].description("Create Record Response").example(Utils.readJsonExample("examples/createEmployeeSuccessfulResponse.json").convertTo[CreateEmployeeResponse])
  private def jsonBodyError    = jsonBody[ErrorResponse].description("Create Record Error").example(Utils.readJsonExample("examples/createEmployeeErrorResponse.json").convertTo[ErrorResponse])

  val endpointDefinition: Endpoint[UsernamePassword, CreateEmployeeRequest, ErrorResponse, CreateEmployeeResponse, Any] =
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

  def serverLogicIfEmailNotInDb(input: CreateEmployeeRequest)(implicit ec: ExecutionContext): Future[Either[ErrorResponse, CreateEmployeeResponse]] = {
    try {
      val timestamp = new Timestamp(new Date().getTime)
      val employee = Employee(
        email = input.email,
        fullName = input.full_name,
        dateOfBirth = input.date_of_birth,
        hobbies = input.hobbies
      )
      val creationStatus = CreateEmployeeUseCase(repository).createEmployee(
        employee,
        timestamp.toString
      )
      creationStatus.flatMap { f =>
        val status = if (f) "completed" else "failed"
        Future.successful(Right(CreateEmployeeResponse(employee.id, input.email, status, timestamp.toString)))
      }
    } catch {
      case e: Exception =>
        logger.info(s"Exception when trying to create an user: ${e.getMessage}")
        Future.successful(Left(ErrorResponse(404, e.getMessage)))
    }
  }

  private def serverLogicCorrectCredentials(input: CreateEmployeeRequest)(implicit ec: ExecutionContext) = {

    val isEmailInDb = EmailAlreadyInDbUseCase(repository).isEmailInDb(input.email)

    isEmailInDb.flatMap { isInDb =>

      if (isInDb.nonEmpty) Future.successful(Left(ErrorResponse(400, s"This email is already being used with the id: ${isInDb.get.id}")))
      else serverLogicIfEmailNotInDb(input)
    }

  }

  private val serverLogic: Either[ErrorResponse, String] => CreateEmployeeRequest => Future[Either[ErrorResponse, CreateEmployeeResponse]] = { check =>in =>
    implicit val ec: ExecutionContext = repository.ec
    logger.info(s"Request received in create-employee endpoint")
    check match {
      case Right(_)    =>
        logger.info(s"Provided credentials are correct")
        serverLogicCorrectCredentials(in)
      case Left(value) =>
        logger.info(s"Provided credentials are incorrect")
        Future.successful(Left(ErrorResponse(value.code, value.message)))
    }

  }

  val endpointToUse =
    endpointDefinition
      .serverSecurityLogicSuccess(securityLogic)
      .serverLogic(serverLogic)

}

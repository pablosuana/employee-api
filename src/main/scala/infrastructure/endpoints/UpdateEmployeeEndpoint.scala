package infrastructure.endpoints

import com.domain.Entities.Employee
import com.domain.interfaces.EmployeeRepository
import com.domain.useCases.UpdateEmployeeUseCase
import infrastructure.dto.client.ErrorResponse
import infrastructure.dto.client.ErrorResponseJsonFormat.errorResponseJsonFormat
import infrastructure.dto.client.updateEmployee.ServiceRequestJsonFormatter.serviceRequestJF
import infrastructure.dto.client.updateEmployee.ServiceResponseJsonFormatter.serviceResponseJF
import infrastructure.dto.client.updateEmployee.{ServiceRequest, ServiceResponse}
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

class UpdateEmployeeEndpoint(repository: EmployeeRepository[PostgresResponse, Employee]) {
  private def jsonBodyRequest  = jsonBody[ServiceRequest].description("Update Record Request")   //.example()
  private def jsonBodyResponse = jsonBody[ServiceResponse].description("Update Record Response") //.example()
  private def jsonBodyError    = jsonBody[ErrorResponse].description("Update Record Error")      //.example()

  val endpointDefinition: Endpoint[UsernamePassword, ServiceRequest, ErrorResponse, ServiceResponse, Any] =
    endpoint.put
      .in("update-employee")
      .in(jsonBodyRequest)
      .errorOut(jsonBodyError)
      .out(jsonBodyResponse)
      .out(statusCode(StatusCode.Ok).description("Successful response"))
      .description("Endpoint used to update info about an employee in the DB using POST")
      .name("update-endpoint")
      .securityIn(auth.basic[UsernamePassword](WWWAuthenticateChallenge("basic")))

  private val securityLogic: UsernamePassword => Future[Either[ErrorResponse, String]] = { credentials =>
    if (credentials.password.contains("password")) {
      Future.successful(Right(credentials.username))
    } else {
      Future.successful(Left(ErrorResponse(401, "Provided credentials are incorrect")))
    }
  }
 // TODO this is not right, double check it
  private def serverLogicCorrectCredentials(input: ServiceRequest, idToUpdate: String)(implicit ec: ExecutionContext) = {
    try {
      val timestamp = new Timestamp(new Date().getTime)
      val updateStatus = UpdateEmployeeUseCase(repository).updateEmployee(
        Employee(
          email = input.email,
          fullName = input.full_name,
          dateOfBirth = input.date_of_birth,
          hobbies = input.hobbies
        ),
        idToUpdate,
        timestamp.toString
      )
      updateStatus.flatMap { f =>
        if (f)
          Future.successful(Right(ServiceResponse(input.id, input.email, timestamp.toString)))
        else
          Future.successful(Left(ErrorResponse(400, "Employee was not updated")))
      }
    } catch {
      case e: Exception => Future.successful(Left(ErrorResponse(404, e.getMessage)))
    }
  }

  private val serverLogic: Either[ErrorResponse, String] => ServiceRequest => Future[Either[ErrorResponse, ServiceResponse]] = { check => in =>
    implicit val ec: ExecutionContext = repository.ec

    check match {
      case Right(_)    => serverLogicCorrectCredentials(in, in.id.toString)
      case Left(value) => Future.successful(Left(ErrorResponse(value.code, value.message)))
    }

  }

  val endpointToUse =
    endpointDefinition
      .serverSecurityLogicSuccess(securityLogic)
      .serverLogic(serverLogic)

}

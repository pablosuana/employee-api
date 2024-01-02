package infrastructure.interfaces

import com.domain.Entities.Employee
import com.domain.interfaces.EmployeeRepository
import com.domain.interfaces.db.DbAsyncOperationsBase
import infrastructure.db._
import infrastructure.dto.db.{PostgresRequest, PostgresResponse}

import java.sql.Connection
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class EmployeeRepositoryImplementation(implicit override val ec: ExecutionContext) extends EmployeeRepository[PostgresResponse, Employee] {

  type QueryToDbType    = String
  type DbConnectionType = Connection
  type DbResponseType   = PostgresResponse

  val dbConnectionConfig: PostgresInMemoryConnectionConf                                   = PostgresInMemoryConnectionConf("application.conf")
  val dbConnectionProvider: PostgresConnectionProvider                                     = new PostgresConnectionProvider(dbConnectionConfig)
  val dbOperations: DbAsyncOperationsBase[DbConnectionType, QueryToDbType, DbResponseType] = new PostgresAsyncOperations(dbConnectionProvider)

  def createEmployee(employee: Employee, createdAt: String): Future[Boolean] = {
    val postgresRequest = PostgresRequest(
      id = employee.id.toString,
      full_name = employee.fullName,
      email = employee.email,
      date_of_birth = employee.dateOfBirth,
      hobbies = employee.hobbies.mkString(","),
      timestamp = createdAt
    )
    val createRecordQuery = CreatePostgresQuery(postgresRequest, dbConnectionConfig.tableName)
    val creationStatus    = dbOperations.updateRecordInDb(createRecordQuery)

    creationStatus.map(f =>
      if (f)
        println(s"Employee with email: ${employee.email} has been created!")
      else println(s"Employee with email: ${employee.email} was not created!")
    )
    creationStatus
  }

  def updateEmployee(employee: Employee, updatedAt: String): Future[Boolean] = {
    val postgresRequest = PostgresRequest(
      id = employee.id.toString,
      full_name = employee.fullName,
      email = employee.email,
      date_of_birth = employee.dateOfBirth,
      hobbies = employee.hobbies.mkString(","),
      timestamp = updatedAt
    )
    val updateRecordQuery = UpdatePostgresQuery(postgresRequest, dbConnectionConfig.tableName)
    val updateStatus      = dbOperations.updateRecordInDb(updateRecordQuery)

    updateStatus.map(f =>
      if (f)
        println(s"Employee with email: ${employee.email} has been updated!")
      else println(s"Employee with email: ${employee.email} was not updated!")
    )
    updateStatus
  }

  override def getAllEmployees: Future[Seq[Employee]] = {

    val queryToGetRecord = GetAllPostgresQuery(dbConnectionConfig.tableName)
    val resultFromQuery  = dbOperations.getRecordsFromDb(queryToGetRecord)

    resultFromQuery.map { r =>
      r.map { x =>
        Employee(
          id = UUID.fromString(x.id),
          email = x.email,
          fullName = x.full_name,
          dateOfBirth = x.date_of_birth,
          hobbies = x.hobbies.split(",")
        )
      }
    }
  }

  def getEmployeeById(id: UUID): Future[Option[PostgresResponse]] = {

    val queryToGetRecord                               = GetOnePostgresQuery(Some(id), None, dbConnectionConfig.tableName)
    val resultFromQuery: Future[Seq[PostgresResponse]] = dbOperations.getRecordsFromDb(queryToGetRecord)
    resultFromQuery.map(_.headOption)
    }

  def deleteEmployee(email: Option[String], id: Option[String]): Future[Boolean] = {
    val queryToDeleteEmail = DeletePostgresQuery(None, email, dbConnectionConfig.tableName)
    val deleteStatus       = dbOperations.updateRecordInDb(queryToDeleteEmail)

    deleteStatus.onComplete(f => println(s"Employee with email: ${email} has been removed!"))

    deleteStatus.map(f =>
      if (f)
        println(s"Employee with email: $email has been deleted!")
      else println(s"Employee with email: $email was not deleted!")
    )
    deleteStatus
  }

}

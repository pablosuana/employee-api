package com.employee.infrastructure.interfaces

import com.employee.domain.entities.Employee
import com.employee.domain.interfaces.EmployeeRepository
import com.employee.domain.interfaces.db.DbAsyncOperationsBase
import com.employee.infrastructure.db.{CreatePostgresQuery, DeletePostgresQuery, GetAllPostgresQuery, GetOnePostgresQuery, PostgresAsyncOperations, PostgresConnectionProvider, PostgresInMemoryConnectionConf, UpdatePostgresQuery}
import com.employee.infrastructure.dto.db.{PostgresRequest, PostgresResponse}
import org.slf4j.LoggerFactory

import java.sql.Connection
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class EmployeeRepositoryImplementation(implicit override val ec: ExecutionContext) extends EmployeeRepository[PostgresResponse, Employee] {

  type QueryToDbType    = String
  type DbConnectionType = Connection
  type DbResponseType   = PostgresResponse

  private val logger                                                                       = LoggerFactory.getLogger(getClass)
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
        logger.info(s"Employee with email: ${employee.email} has been created!")
      else logger.info(s"Employee with email: ${employee.email} was not created!")
    )
    creationStatus
  }

  def updateEmployee(employee: Employee, idToUpdate: String, updatedAt: String): Future[Boolean] = {
    val postgresRequest = PostgresRequest(
      id = idToUpdate,
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
        logger.info(s"Employee with email: ${employee.email} has been updated!")
      else logger.info(s"Employee with email: ${employee.email} was not updated!")
    )
    updateStatus
  }

  override def getAllEmployees: Future[Seq[PostgresResponse]] = {

    val queryToGetRecord = GetAllPostgresQuery(dbConnectionConfig.tableName)
    logger.info(s"Retrieved all employees from DB!")
    dbOperations.getRecordsFromDb(queryToGetRecord)
  }

  // TODO remove duplicated code
  def isEmailInDb(email: String): Future[Option[PostgresResponse]] = {
    val queryToGetRecord                               = GetOnePostgresQuery(None, Some(email), dbConnectionConfig.tableName)
    val resultFromQuery: Future[Seq[PostgresResponse]] = dbOperations.getRecordsFromDb(queryToGetRecord)
    resultFromQuery.map(_.headOption)
  }

  def getEmployeeById(id: UUID): Future[Option[PostgresResponse]] = {

    val queryToGetRecord                               = GetOnePostgresQuery(Some(id), None, dbConnectionConfig.tableName)
    val resultFromQuery: Future[Seq[PostgresResponse]] = dbOperations.getRecordsFromDb(queryToGetRecord)
    resultFromQuery.map(_.headOption)
  }

  def deleteEmployee(email: Option[String], id: Option[String]): Future[Boolean] = {
    val queryToDeleteEmail = DeletePostgresQuery(None, email, dbConnectionConfig.tableName)
    val deleteStatus       = dbOperations.updateRecordInDb(queryToDeleteEmail)

    deleteStatus.onComplete(f => logger.info(s"Employee with email: ${email} has been removed!"))

    deleteStatus.map(f =>
      if (f)
        logger.info(s"Employee with email: $email has been deleted!")
      else logger.info(s"Employee with email: $email was not deleted!")
    )
    deleteStatus
  }

}

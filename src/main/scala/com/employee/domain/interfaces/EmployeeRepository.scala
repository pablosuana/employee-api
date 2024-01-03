package com.employee.domain.interfaces

import com.employee.domain.entities.EmployeeBase
import com.employee.domain.interfaces.db.{DbAsyncOperationsBase, DbConfigBase, DbConnectionProvider, DbResponse}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

trait EmployeeRepository[DbResponseType <: DbResponse, EmployeeType <: EmployeeBase] {

  type QueryToDbType
  type DbConnectionType

  implicit val ec: ExecutionContext

  val dbConnectionConfig: DbConfigBase
  val dbConnectionProvider: DbConnectionProvider[DbConnectionType]
  val dbOperations: DbAsyncOperationsBase[DbConnectionType, QueryToDbType, DbResponseType]

  def getAllEmployees: Future[Seq[DbResponseType]]

  def isEmailInDb(email: String): Future[Option[DbResponseType]]

  def getEmployeeById(id: UUID): Future[Option[DbResponseType]]

  def createEmployee(employee: EmployeeType, createdAt: String): Future[Boolean]

  def deleteEmployee(email: Option[String], id: Option[UUID]): Future[Boolean]

  def updateEmployee(employee: EmployeeType, idToUpdate: String, updatedAt: String): Future[Boolean]
}

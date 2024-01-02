package useCases

import com.domain.Entities.Employee
import com.domain.interfaces.EmployeeRepository
import com.domain.interfaces.db._

import java.util.UUID
import scala.concurrent.ExecutionContext.global
import scala.concurrent.{ExecutionContext, Future}

case class DbResponseExample(id: String, email: String, override val created_at: String, override val updated_at: String) extends DbResponse
class DbConfigExample                                                                                                      extends DbConfigBase

class DbConnectionProviderExample extends DbConnectionProvider[String] {
  val dbConfig: DbConfigBase = new DbConfigExample
  val tableName: String      = "test"
  val getConnection          = "test"
}

class DbAsyncOperationsExample extends DbAsyncOperationsBase[String, String, DbResponseExample] {

  val connectionProvider: DbConnectionProvider[String] = new DbConnectionProviderExample
  val dbResponseExample: DbResponseExample = DbResponseExample(
    "11111111-1111-1111-1111-111111111111",
    "email1@email.com",
    "2023-12-31 00:00:00",
    "2023-12-31 00:00:00"
  )
  def updateRecordInDb(updateQuery: DbQuery[String]): Future[Boolean]              = Future.successful(true)
  def getRecordsFromDb(getQuery: GetQuery[String]): Future[Seq[DbResponseExample]] = Future.successful(Seq(dbResponseExample))
}

class TestEmployeeRepository extends EmployeeRepository[DbResponseExample, Employee] {

  type QueryToDbType    = String
  type DbConnectionType = String
  type DbResponseType   = DbResponseExample

  implicit val ec: ExecutionContext = global
  //TODO this shouldn't be duplicated
  val dbResponseExample: DbResponseExample = DbResponseExample(
    "11111111-1111-1111-1111-111111111111",
    "email1@email.com",
    "2023-12-31 00:00:00",
    "2023-12-31 00:00:00"
  )

  val dbResponseExample2: DbResponseExample = DbResponseExample(
    "22211111-1111-1111-1111-111111111111",
    "email2@email.com",
    "2023-12-31 00:00:00",
    "2023-12-31 00:00:00"
  )

  val dbConnectionConfig: DbConfigBase                                                     = new DbConfigExample
  val dbConnectionProvider: DbConnectionProvider[DbConnectionType]                         = new DbConnectionProviderExample
  val dbOperations: DbAsyncOperationsBase[DbConnectionType, QueryToDbType, DbResponseType] = new DbAsyncOperationsExample

  def getAllEmployees: Future[Seq[DbResponseExample]] = Future.successful(
    Seq(dbResponseExample, dbResponseExample2)
  )

  def isEmailInDb(email: String): Future[Option[DbResponseExample]] =
    Future.successful(
      Some(dbResponseExample)
    )

  def getEmployeeById(id: UUID): Future[Option[DbResponseExample]] =
    Future.successful(
      Some(dbResponseExample)
    )

  def createEmployee(employee: Employee, createdAt: String): Future[Boolean] = Future.successful(true)

  def deleteEmployee(email: Option[String], id: Option[String]): Future[Boolean] = Future.successful(true)

  def updateEmployee(employee: Employee, updatedAt: String): Future[Boolean] = Future.successful(true)
}

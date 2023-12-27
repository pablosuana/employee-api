package useCases

import com.domain.EmployeeBase
import interfaces.{CreateQuery, DbAsyncOperationsBase}

import scala.concurrent.Future

abstract class CreateRecord[T, U](employee: EmployeeBase, dbAsyncConnector: DbAsyncOperationsBase[T, U]) {

  def dbQueryCreator(employee: EmployeeBase): CreateQuery[U]
  def creation(employee: EmployeeBase, dbAsyncConnector: DbAsyncOperationsBase[T, U]): Future[String] = dbAsyncConnector.createRecordIntoDb(dbQueryCreator(employee))
}
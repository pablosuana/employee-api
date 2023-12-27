package interfaces

import scala.concurrent.Future

trait DbAsyncOperationsBase[DbConnectionType, ExpectedQueryType] {

  val connectionProvider: DbConnectionProvider[DbConnectionType]
  def getRecordFromDb(getQuery: GetQuery[ExpectedQueryType]): Future[DbResponse]

  def createRecordIntoDb(createQuery: CreateQuery[ExpectedQueryType]): Future[String]

  def deleteRecordFromDb(deleteQuery: DeleteQuery[ExpectedQueryType]): Future[String]

  def updateRecordInDb(updateQuery: UpdateQuery[ExpectedQueryType]): Future[String]

}

trait DbConnectionProvider[T] {
  val dbConfig: DbConfigBase
  val getConnection: T
}

trait DbConfigBase

class DbResponse(r: String)

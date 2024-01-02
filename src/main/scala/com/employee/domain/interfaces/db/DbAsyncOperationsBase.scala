package com.employee.domain.interfaces.db

import scala.concurrent.Future

trait DbAsyncOperationsBase[DbConnectionType, ExpectedQueryType, +DbRes <: DbResponse] {

  val connectionProvider: DbConnectionProvider[DbConnectionType]
  def updateRecordInDb(updateQuery: DbQuery[ExpectedQueryType]): Future[Boolean]
  def getRecordsFromDb(getQuery: GetQuery[ExpectedQueryType]): Future[Seq[DbRes]]
}

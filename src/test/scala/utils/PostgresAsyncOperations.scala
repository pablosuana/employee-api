package utils

import com.domain.EmployeeBase
import interfaces.{CreateQuery, DbAsyncOperationsBase, DbResponse, DeleteQuery, GetQuery, UpdateQuery}

import java.sql.{Connection, Statement}
import scala.concurrent.Future

class PostgresAsyncOperations(employeeBase: EmployeeBase)(override val connectionProvider: PostgresInMemoryConnectionProviderProvider)
  extends DbAsyncOperationsBase[Connection, String] {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  override def updateRecordInDb(updateQuery: UpdateQuery[String]): Future[String] = {
    val dbConnection = connectionProvider.getConnection
    val stm: Statement = dbConnection.createStatement

    Future {
      try {
        stm.execute(updateQuery.queryMapper(employeeBase))
        s"Record successfully updated with id: ${employeeBase.id}"
      } catch {
        case e: Exception => s"Something failed. Exception: $e. Employee id: ${employeeBase.id}"
      }
    }
  }

  override def createRecordIntoDb(createQuery: CreateQuery[String]): Future[String] = {
    val dbConnection = connectionProvider.getConnection
    val stm: Statement = dbConnection.createStatement

    Future {
      try {
        stm.execute(createQuery.queryMapper(employeeBase))
        s"Record successfully inserted with id: ${employeeBase.id}"
      } catch {
        case e: Exception => s"Something failed. Exception: $e. Employee id: ${employeeBase.id}"
      }
    }
  }

  override def deleteRecordFromDb(deleteQuery: DeleteQuery[String]): Future[String] = {
    val dbConnection = connectionProvider.getConnection
    val stm: Statement = dbConnection.createStatement

    Future {
      try {
        stm.execute(deleteQuery.queryMapper(employeeBase))
        s"Record successfully deleted with id: ${employeeBase.id}"
      } catch {
        case e: Exception => s"Something failed. Exception: $e. Employee id: ${employeeBase.id}"
      }
    }
  }

  override def getRecordFromDb(getQuery: GetQuery[String]): Future[DbResponse] = {
    val dbConnection = connectionProvider.getConnection
    val stm: Statement = dbConnection.createStatement
    val rs = stm.executeQuery(getQuery.queryMapper(employeeBase))

    Future {
      try {
        if (rs.next())
          new DbResponse(s"Employee found: ${rs.getString("id")}")
        else new DbResponse("Employee not found")
      } catch {
        case e: Exception => new DbResponse(s"Something failed. Exception: $e. Employee id: ${employeeBase.id}")
      }
    }
  }

}
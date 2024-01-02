package infrastructure.db

import com.domain.interfaces.db.{DbAsyncOperationsBase, DbQuery, GetQuery}
import infrastructure.dto.db.PostgresResponse
import infrastructure.dto.db.PostgresResponseJsonFormat.postgresResponseJsonFormat
import spray.json._

import java.sql.{Connection, Statement}
import scala.concurrent.Future

class PostgresAsyncOperations(override val connectionProvider: PostgresConnectionProvider)
  extends DbAsyncOperationsBase[Connection, String, PostgresResponse] {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  override def updateRecordInDb(updateQuery: DbQuery[String]): Future[Boolean] = {
    val dbConnection = connectionProvider.getConnection
    val stm: Statement = dbConnection.createStatement

    Future {
      try {
        val rowsUpdated: Int = stm.executeUpdate(updateQuery.queryMapper)
        if (rowsUpdated > 0) {
          println(s"Record successfully updated with ${updateQuery.getClass}: ${updateQuery.queryMapper}")
          true
        }
        else {
          println(s"Record not updated with ${updateQuery.getClass}: ${updateQuery.queryMapper}")
          false
        }
      } catch {
        case e: Exception => println(s"Something failed. Exception: $e. Query: ${updateQuery.queryMapper}")
        false
      }
    }
  }

  override def getRecordsFromDb(getQuery: GetQuery[String]): Future[Seq[PostgresResponse]] = {
    val dbConnection = connectionProvider.getConnection
    val stm: Statement = dbConnection.createStatement
    val rs = stm.executeQuery(getQuery.queryMapper)
    var queryResults = Seq.empty[PostgresResponse]

    Future {
      while (rs.next()) {
        val result: PostgresResponse = s"""{
         |"id": "${rs.getString("id")}",
         |"full_name": "${rs.getString("full_name")}",
         |"email": "${rs.getString("email")}",
         |"date_of_birth": "${rs.getString("date_of_birth")}",
         |"hobbies": "${rs.getString("hobbies")}",
         |"created_at": "${rs.getString("created_at")}",
         |"updated_at": "${rs.getString("updated_at")}"
         |}
         |""".stripMargin.parseJson.convertTo[PostgresResponse]
        queryResults = queryResults :+ result
      }
      queryResults
    }
  }

}
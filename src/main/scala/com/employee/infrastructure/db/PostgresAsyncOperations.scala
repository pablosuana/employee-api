package com.employee.infrastructure.db

import com.employee.domain.interfaces.db.{DbAsyncOperationsBase, DbQuery, GetQuery}
import com.employee.infrastructure.dto.db.PostgresResponse
import com.employee.infrastructure.dto.db.PostgresResponseJsonFormat.postgresResponseJsonFormat
import org.slf4j.LoggerFactory
import spray.json._

import java.sql.{Connection, Statement}
import scala.concurrent.Future

class PostgresAsyncOperations(override val connectionProvider: PostgresConnectionProvider) extends DbAsyncOperationsBase[Connection, String, PostgresResponse] {

  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
  private val logger                                 = LoggerFactory.getLogger(getClass)

  override def updateRecordInDb(updateQuery: DbQuery[String]): Future[Boolean] = {
    val dbConnection   = connectionProvider.getConnection
    val stm: Statement = dbConnection.createStatement

    Future {
      try {
        val rowsUpdated: Int = stm.executeUpdate(updateQuery.queryMapper)
        if (rowsUpdated > 0) {
          logger.info(s"Record successfully updated with ${updateQuery.getClass}: ${updateQuery.queryMapper}")
          true
        } else {
          logger.info(s"Record not updated with ${updateQuery.getClass}: ${updateQuery.queryMapper}")
          false
        }
      } catch {
        case e: Exception =>
          logger.info(s"Something failed. Exception: $e. Query: ${updateQuery.queryMapper}")
          false
      }
    }
  }

  override def getRecordsFromDb(getQuery: GetQuery[String]): Future[Seq[PostgresResponse]] = {
    val dbConnection   = connectionProvider.getConnection
    val stm: Statement = dbConnection.createStatement
    val rs             = stm.executeQuery(getQuery.queryMapper)
    var queryResults   = Seq.empty[PostgresResponse]
    // TODO this should be refactored, not looking nice
    Future {
      while (rs.next()) {
        val result: JsValue = s"""{
         |"id": "${rs.getString("id")}",
         |"full_name": "${rs.getString("full_name")}",
         |"email": "${rs.getString("email")}",
         |"date_of_birth": "${rs.getString("date_of_birth")}",
         |"hobbies": "${rs.getString("hobbies")}",
         |"created_at": "${rs.getString("created_at")}",
         |"updated_at": "${rs.getString("updated_at")}"
         |}
         |""".stripMargin.parseJson

        logger.info(s"Build PostgresResponse response: $result")

        val postgresResponse = result.convertTo[PostgresResponse]
        queryResults = queryResults :+ postgresResponse
      }
      queryResults
    }
  }

}

package infrastructure

import com.domain.Entities.Employee
import infrastructure.db._
import infrastructure.dto.db.PostgresRequest
import infrastructure.dto.db.PostgresResponseJsonFormat.postgresResponseJsonFormat
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import spray.json._

import java.sql._
import java.time.{LocalDateTime, ZoneId}
import java.util.UUID
import scala.concurrent.ExecutionContext.global

class TestDbAsyncOperationsBase extends AnyFunSuite with Matchers with BeforeAndAfterAll with ScalaFutures {

  implicit val ec = global

  val employee1        = Employee("email1@email.com", "fullname1", "1990-01-01", Seq.empty, Some("seed1"))
  val employee2        = Employee("email2@email.com", "fullname2", "2000-10-12", Seq.empty, Some("seed2"))
  val dbConnection     = new PostgresConnectionProvider(PostgresInMemoryConnectionConf("application-test.conf"))
  val tableName        = dbConnection.tableName
  val postgresInstance = new PostgresAsyncOperations(dbConnection)

  test("We can add records to a chosen database") {

    val specificDateTime = LocalDateTime.of(2023, 12, 31, 0, 0, 0)
    val specificTimeInMillis = specificDateTime.atZone(ZoneId.systemDefault()).toInstant.toEpochMilli

    val createdAt = new Timestamp(specificTimeInMillis).toString
    val postgresRequest1 = PostgresRequest(
      id = employee1.id.toString,
      full_name = employee1.fullName,
      email = employee1.email,
      date_of_birth = employee1.dateOfBirth,
      hobbies = employee1.hobbies.mkString(","),
      timestamp = createdAt
    )
    val postgresRequest2 = PostgresRequest(
      id = employee2.id.toString,
      full_name = employee2.fullName,
      email = employee2.email,
      date_of_birth = employee2.dateOfBirth,
      hobbies = employee2.hobbies.mkString(","),
      timestamp = createdAt
    )

    val queryToCreateRecordInDB  = CreatePostgresQuery(postgresRequest1, tableName)
    val queryToCreateRecordInDB2 = CreatePostgresQuery(postgresRequest2, tableName)
    val fut1                     = postgresInstance.updateRecordInDb(queryToCreateRecordInDB)
    val fut2                     = postgresInstance.updateRecordInDb(queryToCreateRecordInDB2)

    val testingStm: Statement = dbConnection.getConnection.createStatement

    val rs = for {
      f1 <- fut1
      f2 <- fut2
    } yield {
      testingStm.executeQuery("select id from test_table")
    }

    whenReady(rs) { r =>
      var resultList = Seq.empty[String]

      while (r.next()) {
        val extractedId = r.getString("id")
        resultList = resultList :+ extractedId
      }

      resultList.length shouldBe 2
      resultList.sortWith(_ < _) shouldBe Seq("9289941f-8a42-30fd-ac05-f11fab6bc867", "f2aee81c-67e9-36a3-a0fe-adc451f05d9f")

    }
  }

  test("We can update records to a chosen database") {
    val newemployee2 = Employee("email2@email.com", "fullname2", "2000-10-12", Seq.empty, Some("seed2"))

    val specificDateTime = LocalDateTime.of(2024, 1, 1, 0, 0, 0)
    val specificTimeInMillis = specificDateTime.atZone(ZoneId.systemDefault()).toInstant.toEpochMilli

    val updatedAt = new Timestamp(specificTimeInMillis).toString

    val postgresRequest2 = PostgresRequest(
      id = newemployee2.id.toString,
      full_name = newemployee2.fullName,
      email = newemployee2.email,
      date_of_birth = newemployee2.dateOfBirth,
      hobbies = newemployee2.hobbies.mkString(","),
      timestamp = updatedAt
    )
    val queryToUpdateRecordInDB2 = UpdatePostgresQuery(postgresRequest2, tableName)
    val fut2                     = postgresInstance.updateRecordInDb(queryToUpdateRecordInDB2)

    val rs = for {
      f2 <- fut2
    } yield {
      val testingStm: Statement = dbConnection.getConnection.createStatement
      testingStm.executeQuery("select * from test_table")
    }

    whenReady(rs) { r =>
      var resultList = Seq.empty[String]

      while (r.next()) {
        val extractedId = r.getString("id")
        resultList = resultList :+ extractedId
      }

      resultList.length shouldBe 2
      resultList.sortWith(_ < _) shouldBe Seq("9289941f-8a42-30fd-ac05-f11fab6bc867", "f2aee81c-67e9-36a3-a0fe-adc451f05d9f")

    }
  }

  test("We can get a record by id") {

    val queryToGetRecord = GetOnePostgresQuery(Some(UUID.fromString("9289941f-8a42-30fd-ac05-f11fab6bc867")), None, tableName)
    val fut2             = postgresInstance.getRecordsFromDb(queryToGetRecord)

    whenReady(fut2) { r =>
      val jsonResult = r.map(_.toJson.sortedPrint)
      jsonResult shouldBe Seq(
        """{"updated_at": "2023-12-31 00:00:00", "created_at": "2023-12-31 00:00:00","date_of_birth":"1990-01-01","email":"email1@email.com","full_name":"fullname1","hobbies":"","id":"9289941f-8a42-30fd-ac05-f11fab6bc867"}""".stripMargin.parseJson.sortedPrint
      )
    }
  }

  test("We can get a record by email") {

    val queryToGetRecord = GetOnePostgresQuery(None, Some("email1@email.com"), tableName)
    val fut2             = postgresInstance.getRecordsFromDb(queryToGetRecord)

    whenReady(fut2) { r =>
      val jsonResult = r.map(_.toJson.sortedPrint)
      jsonResult shouldBe Seq(
        """{"updated_at": "2023-12-31 00:00:00", "created_at": "2023-12-31 00:00:00","date_of_birth":"1990-01-01","email":"email1@email.com","full_name":"fullname1","hobbies":"","id":"9289941f-8a42-30fd-ac05-f11fab6bc867"}""".parseJson.sortedPrint
      )
    }
  }

  test("We can get all the records") {

    val queryToGetRecord = GetAllPostgresQuery(tableName)
    val fut2             = postgresInstance.getRecordsFromDb(queryToGetRecord)

    whenReady(fut2) { r =>
      val jsonResult = r.map(_.toJson.sortedPrint)
      jsonResult.sortWith(_ < _) shouldBe Seq(
        """{
          |  "updated_at": "2023-12-31 00:00:00",
          |  "created_at": "2023-12-31 00:00:00",
          |  "date_of_birth": "1990-01-01",
          |  "email": "email1@email.com",
          |  "full_name": "fullname1",
          |  "hobbies": "",
          |  "id": "9289941f-8a42-30fd-ac05-f11fab6bc867"
          |  }""".stripMargin.parseJson.sortedPrint,
        """{
          |  "updated_at": "2024-01-01 00:00:00",
          |  "created_at": "2023-12-31 00:00:00",
          |  "date_of_birth": "2000-10-12",
          |  "email": "email2@email.com",
          |  "full_name": "fullname2",
          |  "hobbies": "",
          |  "id": "f2aee81c-67e9-36a3-a0fe-adc451f05d9f"
          |}""".stripMargin.parseJson.sortedPrint
      )
    }
  }

  test("We can remove records from a chosen database") {

    val queryToDeleteId    = DeletePostgresQuery(Some(UUID.fromString("f2aee81c-67e9-36a3-a0fe-adc451f05d9f")), None, tableName)
    val queryToDeleteEmail = DeletePostgresQuery(None, Some("email1@email.com"), tableName)
    val fut1               = postgresInstance.updateRecordInDb(queryToDeleteId)
    val fut2               = postgresInstance.updateRecordInDb(queryToDeleteEmail)

    val rs = for {
      f1 <- fut1
      f2 <- fut2
    } yield {
      val testingStm: Statement = dbConnection.getConnection.createStatement
      testingStm.executeQuery("select * from test_table")
    }
    whenReady(rs) { r =>
      var resultList = Seq.empty[String]

      while (r.next()) {
        val extractedId = r.getString("id")
        resultList = resultList :+ extractedId
      }

      resultList.length shouldBe 0
    }
  }

}

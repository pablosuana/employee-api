package useCases

import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import utils.{PostgresInMemoryConnectionProviderProvider, PostgresInMemoryConnectionConf}

import java.sql._

class TestDbAsyncOperationsBase extends AnyFunSuite with Matchers with BeforeAndAfterAll {

  test("We can add records to a chosen database") {

    val dbConnection = new PostgresInMemoryConnectionProviderProvider(PostgresInMemoryConnectionConf("application-test.conf")).getConnection

    var row1InsertionCheck = false

    val stm: Statement = dbConnection.createStatement
    val sql: String =
      """
        |INSERT INTO test_table ("id", "full_name", "email", "hobbies", "date_of_birth") VALUES ('1', 'A', 'email@example.com', 'AA', '2023-01-01');
        |""".stripMargin

    stm.execute(sql)
    val rs = stm.executeQuery("select * from test_table where id = '1'")

    if (rs.next()) {
      row1InsertionCheck = ("1" == rs.getString("id"))

    }

    assert(row1InsertionCheck, "Data not inserted")
    //    val a = new PostgresInMemoryOperations("""
    //INSERT INTO table_name (id, full_name, email, hobbies, date_of_birth, created_at)
    //VALUES (11111111-1111-1111-1111-111111111111, full_name, email, LIST(hobbies), 1999-09-09);"""
    //                                             )(dbConnection.dbConnection)
    //    Thread.sleep(1000000)
    //  a.createRecordIntoDb(Employee(
    //    UUID.fromString("11111111-1111-1111-1111-111111111111"),
    //    "email@example.com",
    //    "fullName",
    //    "1900-01-31",
    //    Seq("hobbies")
    //  ))
    //  }

  }
}

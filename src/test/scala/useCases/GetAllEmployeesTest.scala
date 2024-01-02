package useCases

import com.domain.useCases.GetAllEmployeesUseCase
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class GetAllEmployeesTest extends AnyFunSuite with Matchers with ScalaFutures {

  val repository = new TestEmployeeRepository

  test(s"GetAllEmployees class has the expected methods") {

    val getAllEmployees: Future[Seq[DbResponseExample]] = GetAllEmployeesUseCase(repository).getAllEmployees

    whenReady(getAllEmployees) { f =>
      f shouldBe Seq(
        DbResponseExample(
          "11111111-1111-1111-1111-111111111111",
          "email1@email.com",
          "2023-12-31 00:00:00",
          "2023-12-31 00:00:00"
        ),
        DbResponseExample(
          "22211111-1111-1111-1111-111111111111",
          "email2@email.com",
          "2023-12-31 00:00:00",
          "2023-12-31 00:00:00"
        )
      )
    }
  }
}

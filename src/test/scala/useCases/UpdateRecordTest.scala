package useCases

import com.domain.Entities.Employee
import com.domain.useCases.UpdateEmployeeUseCase
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import java.util.UUID
import scala.concurrent.Future

class UpdateRecordTest extends AnyFunSuite with Matchers with ScalaFutures{

  val repository = new TestEmployeeRepository

  test(s"UpdateRecord class has the expected methods") {
    val employee = Employee("email1@email.com", "fullname1", "1990-01-01", Seq.empty)

    val updateEmployee: Future[Boolean] = UpdateEmployeeUseCase(repository).updateEmployee(employee, "2023-12-31 00:00:00")

    whenReady(updateEmployee) { f =>
      f shouldBe true
    }
  }
}

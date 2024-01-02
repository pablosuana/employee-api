package useCases

import com.employee.domain.entities.Employee
import com.employee.domain.useCases.UpdateEmployeeUseCase
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class UpdateRecordTest extends AnyFunSuite with Matchers with ScalaFutures{

  val repository = new TestEmployeeRepository

  test(s"UpdateRecord class has the expected methods") {
    val employee = Employee("email1@email.com", "fullname1", "1990-01-01", Seq.empty, Some("seed1"))

    val updateEmployee: Future[Boolean] = UpdateEmployeeUseCase(repository).updateEmployee(employee, "9289941f-8a42-30fd-ac05-f11fab6bc867", "2023-12-31 00:00:00")

    whenReady(updateEmployee) { f =>
      f shouldBe true
    }
  }
}

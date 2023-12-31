package useCases

import com.employee.domain
import com.employee.domain.entities.Employee
import com.employee.domain.useCases.CreateEmployeeUseCase
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import java.util.UUID
import scala.concurrent.Future

class CreateRecordTest extends AnyFunSuite with Matchers with ScalaFutures{

  val repository = new TestEmployeeRepository

  test(s"CreateRecord class has the expected methods") {
    val employee = Employee("email1@email.com", "fullname1", "1990-01-01", Seq.empty)

    val createEmployee: Future[Boolean] = domain.useCases.CreateEmployeeUseCase(repository).createEmployee(employee, "2023-12-31 00:00:00")

    whenReady(createEmployee) { f =>
      f shouldBe true
    }
  }
}

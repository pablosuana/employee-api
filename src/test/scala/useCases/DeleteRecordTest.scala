package useCases

import com.employee.domain
import com.employee.domain.useCases.DeleteEmployeeUseCase
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class DeleteRecordTest extends AnyFunSuite with Matchers with ScalaFutures {

  val repository = new TestEmployeeRepository

  test(s"DeleteRecord class has the expected methods") {

    val deleteEmployee: Future[Boolean] = domain.useCases.DeleteEmployeeUseCase(repository).deleteEmployee(Some("11111111-1111-1111-1111-111111111111"), None)

    whenReady(deleteEmployee) { f =>
      f shouldBe true
    }
  }
}

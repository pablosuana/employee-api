package useCases

import com.employee.domain
import com.employee.domain.useCases.GetEmployeeUseCase
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class GetRecordTest extends AnyFunSuite with Matchers with ScalaFutures{

  val repository = new TestEmployeeRepository

  test(s"GetRecord class has the expected methods") {

    val getEmployee: Future[Option[DbResponseExample]] = domain.useCases.GetEmployeeUseCase(repository).getEmployee("11111111-1111-1111-1111-111111111111")

    whenReady(getEmployee) { f =>
      f shouldBe Some(DbResponseExample("11111111-1111-1111-1111-111111111111", "email1@email.com", "2023-12-31 00:00:00", "2023-12-31 00:00:00"))
    }
  }
}

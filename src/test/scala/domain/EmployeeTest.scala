package domain

import com.domain.Entities.Employee
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import scala.language.postfixOps
import scala.reflect.runtime.universe.{MethodSymbol, typeOf}

class EmployeeTest extends AnyFunSuite with Matchers {

  test(s"Employee class has the expected fields and expected types") {

    val expectedFields = Map(
      "id"          -> "java.util.UUID",
      "email"       -> "String",
      "fullName"    -> "String",
      "dateOfBirth" -> "String",
      "hobbies"     -> "Seq[String]"
    )
    val classType = typeOf[Employee]
    val actualFields = classType.members.collect {
      case m: MethodSymbol if m.isCaseAccessor =>
        m.name.toString -> m.returnType.toString
    }.toMap

    actualFields.keys shouldBe expectedFields.keys
    actualFields.map { field =>
      expectedFields(field._1) shouldBe field._2
    }
  }
  test(s"Date of birth is accepted in format YYYY-MM-DD") {

    val employee = Employee(
      UUID.fromString("11111111-1111-1111-1111-111111111111"),
      "email@example.com",
      "fullName",
      "1900-01-31",
      Seq("hobbies")
    )

    employee should not be null
    employee.id shouldBe UUID.fromString("11111111-1111-1111-1111-111111111111")
    employee.email shouldBe "email@example.com"
    employee.dateOfBirth shouldBe LocalDate.parse("1900-01-31", DateTimeFormatter.ISO_LOCAL_DATE).toString
    employee.hobbies shouldBe Seq("hobbies")
  }

  test(s"Date of birth is not accepted in format dd-MM-yyyy") {

    val expectedErrorMessage = "requirement failed: Date of birth is accepted only in YYYY-MM-DD format"
    val actualException = intercept[IllegalArgumentException] {
      Employee(
        "email@example.com",
        "fullName",
        "31-01-1900",
        Seq("hobbies")
      )
    }

    actualException.getMessage shouldBe expectedErrorMessage
  }

  test(s"Email is not accepted if is not compliant with regexp") {

    val expectedErrorMessage = "requirement failed: Email has to be compliant with regex: ^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    val actualException = intercept[IllegalArgumentException] {
      Employee(
        "email-example.com",
        "fullName",
        "1900-01-31",
        Seq("hobbies")
      )
    }

    actualException.getMessage shouldBe expectedErrorMessage
  }
}

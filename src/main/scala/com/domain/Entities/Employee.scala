package com.domain.Entities

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import scala.util.Try

case class Employee(
  id: UUID,
  email: String,
  fullName: String,
  dateOfBirth: String,
  hobbies: Seq[String]
) extends EmployeeBase

object Employee {
  def apply(id: UUID, email: String, fullName: String, dateOfBirth: String, hobbies: Seq[String]): Employee = {

    val formattedDob = Try(LocalDate.parse(dateOfBirth, DateTimeFormatter.ISO_LOCAL_DATE)).toOption
    val emailRegex   = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    val pattern      = emailRegex.r

    require(formattedDob.nonEmpty, "Date of birth is accepted only in YYYY-MM-DD format")
    require(pattern.findFirstIn(email).isDefined, "Email has to be compliant with regex: ^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")

    new Employee(id, email, fullName, dateOfBirth, hobbies)
  }
}

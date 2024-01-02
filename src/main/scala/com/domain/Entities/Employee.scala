package com.domain.Entities

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import scala.util.Try

class Employee private (
  val id: UUID,
  val email: String,
  val fullName: String,
  val dateOfBirth: String,
  val hobbies: Seq[String]
) extends EmployeeBase

object Employee {

  private def generateUUID(seed: Option[String]): UUID = {
    seed match {
      case Some(value) =>
        val seedUUID = UUID.nameUUIDFromBytes(value.getBytes)
        UUID.fromString(seedUUID.toString)
      case None => UUID.randomUUID()
    }
  }

  def apply(email: String, fullName: String, dateOfBirth: String, hobbies: Seq[String], uuidSeed: Option[String] = None): Employee = {

    val formattedDob: Option[LocalDate] = Try(LocalDate.parse(dateOfBirth, DateTimeFormatter.ISO_LOCAL_DATE)).toOption
    val emailRegex   = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    val pattern      = emailRegex.r

    require(formattedDob.nonEmpty, "Date of birth is accepted only in YYYY-MM-DD format")
    require(pattern.findFirstIn(email).isDefined, "Email has to be compliant with regex: ^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")

    new Employee(generateUUID(uuidSeed), email, fullName, dateOfBirth, hobbies)
  }
}

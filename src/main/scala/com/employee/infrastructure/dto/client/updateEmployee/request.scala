package com.employee.infrastructure.dto.client.updateEmployee

import spray.json.RootJsonFormat

import java.util.UUID
// TODO improve query to not need to pass all of the fields. Query needs to be updated
case class UpdateEmployeeRequest(
  id: UUID,
  email: String,
  full_name: String,
  date_of_birth: String,
  hobbies: Seq[String]
)

object RequestJsonFormatter {

  import com.employee.infrastructure.dto.client.CommonJsonFormats._
  import spray.json.DefaultJsonProtocol._

  implicit val updateEmployeeRequestJF: RootJsonFormat[UpdateEmployeeRequest] = jsonFormat5(UpdateEmployeeRequest)
}

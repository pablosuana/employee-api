package com.employee.infrastructure.dto.client.createEmployee

import spray.json.RootJsonFormat

case class CreateEmployeeRequest(
  email: String,
  full_name: String,
  date_of_birth: String,
  hobbies: Seq[String]
)

object RequestJsonFormatter {

  import spray.json.DefaultJsonProtocol._

  implicit val createEmploymentRequestJF: RootJsonFormat[CreateEmployeeRequest] = jsonFormat4(CreateEmployeeRequest)
}

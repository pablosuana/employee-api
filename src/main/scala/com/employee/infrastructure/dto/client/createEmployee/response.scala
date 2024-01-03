package com.employee.infrastructure.dto.client.createEmployee

import spray.json.RootJsonFormat

import java.util.UUID

case class CreateEmployeeResponse(
  id: UUID = UUID.randomUUID(),
  email: String,
  status: String,
  updated_at: String
)

object ResponseJsonFormatter {

  import com.employee.infrastructure.dto.client.CommonJsonFormats._
  import spray.json.DefaultJsonProtocol._

  implicit val createaEmployeeResponseJF: RootJsonFormat[CreateEmployeeResponse] = jsonFormat4(CreateEmployeeResponse)
}

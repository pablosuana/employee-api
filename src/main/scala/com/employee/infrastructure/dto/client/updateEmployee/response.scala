package com.employee.infrastructure.dto.client.updateEmployee

import spray.json.DefaultJsonProtocol.{jsonFormat3, StringJsonFormat}
import spray.json.RootJsonFormat

import java.util.UUID

case class UpdateEmployeeResponse(
  id: UUID = UUID.randomUUID(),
  email: String,
  updated_at: String
)

object ResponseJsonFormatter {

  import com.employee.infrastructure.dto.client.CommonJsonFormats._

  implicit val updateEmployeeResponseJF: RootJsonFormat[UpdateEmployeeResponse] = jsonFormat3(UpdateEmployeeResponse)
}

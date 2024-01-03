package com.employee.infrastructure.dto.client.deleteEmployee

import spray.json.DefaultJsonProtocol.{jsonFormat1, StringJsonFormat}
import spray.json.RootJsonFormat

case class DeleteEmployeeResponse(
  status: String
)

object ResponseJsonFormatter {

  implicit val deleteEmployeeResponseJF: RootJsonFormat[DeleteEmployeeResponse] = jsonFormat1(DeleteEmployeeResponse)
}

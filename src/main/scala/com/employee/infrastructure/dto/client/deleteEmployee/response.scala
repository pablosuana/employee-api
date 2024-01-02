package com.employee.infrastructure.dto.client.deleteEmployee

import spray.json.DefaultJsonProtocol.{StringJsonFormat, jsonFormat1}
import spray.json.RootJsonFormat

case class ServiceResponse(
  status: String
)

object ServiceResponseJsonFormatter {

  implicit val serviceResponseDeleteEmployeeJF: RootJsonFormat[ServiceResponse] = jsonFormat1(ServiceResponse)
}

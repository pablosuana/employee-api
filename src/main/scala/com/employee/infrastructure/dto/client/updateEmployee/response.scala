package com.employee.infrastructure.dto.client.updateEmployee

import spray.json.DefaultJsonProtocol.{StringJsonFormat, jsonFormat3}
import spray.json.RootJsonFormat

import java.util.UUID

case class ServiceResponse(
  id: UUID = UUID.randomUUID(),
  email: String,
  updated_at: String
)

object ServiceResponseJsonFormatter {

  import com.employee.infrastructure.dto.client.CommonJsonFormats._

  implicit val serviceResponseJF: RootJsonFormat[ServiceResponse] = jsonFormat3(ServiceResponse)
}

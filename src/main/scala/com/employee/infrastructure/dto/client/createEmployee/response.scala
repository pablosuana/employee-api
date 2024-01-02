package com.employee.infrastructure.dto.client.createEmployee

import spray.json.RootJsonFormat

import java.util.UUID

case class ServiceResponse(
  id: UUID = UUID.randomUUID(),
  email: String,
  status: String,
  updated_at: String
)

object ServiceResponseJsonFormatter {

  import com.employee.infrastructure.dto.client.CommonJsonFormats._
  import spray.json.DefaultJsonProtocol._

  implicit val serviceResponseJF: RootJsonFormat[ServiceResponse] = jsonFormat4(ServiceResponse)
}

package com.employee.infrastructure.dto.client.deleteEmployee

import spray.json.RootJsonFormat

import java.util.UUID

case class DeleteEmployeeRequest(
  id: Option[UUID],
  email: Option[String]
)

object RequestJsonFormatter {

  import com.employee.infrastructure.dto.client.CommonJsonFormats._
  import spray.json.DefaultJsonProtocol._

  implicit val deleteEmployeeRequestJF: RootJsonFormat[DeleteEmployeeRequest] = jsonFormat2(DeleteEmployeeRequest)
}

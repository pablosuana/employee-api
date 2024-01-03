package com.employee.infrastructure.dto.client.getEmployee

import spray.json.DefaultJsonProtocol.{immSeqFormat, jsonFormat1, jsonFormat2, jsonFormat5, StringJsonFormat}
import spray.json.RootJsonFormat

import java.util.UUID
case class Result(
  id: UUID,
  email: String,
  full_name: String,
  date_of_birth: String,
  hobbies: Seq[String]
)

case class Metadata(
  updated_at: String
)

case class GetEmployeeResponse(
  result: Result,
  metadata: Metadata
)

object ServiceResponseJsonFormatter {

  import com.employee.infrastructure.dto.client.CommonJsonFormats._

  implicit val metadataJF: RootJsonFormat[Metadata]                       = jsonFormat1(Metadata)
  implicit val resultJF: RootJsonFormat[Result]                           = jsonFormat5(Result)
  implicit val getEmployeeResponseJF: RootJsonFormat[GetEmployeeResponse] = jsonFormat2(GetEmployeeResponse)
}

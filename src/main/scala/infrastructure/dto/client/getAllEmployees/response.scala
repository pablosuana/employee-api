package infrastructure.dto.client.getAllEmployees

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

case class EmployeeData(
  result: Result,
  metadata: Metadata
)

case class ServiceResponse(employees: Seq[EmployeeData])

object ServiceResponseJsonFormatter {

  import infrastructure.dto.client.CommonJsonFormats._

  implicit val metadataJF: RootJsonFormat[Metadata]                          = jsonFormat1(Metadata)
  implicit val resultJF: RootJsonFormat[Result]                              = jsonFormat5(Result)
  implicit val employeeDataJF: RootJsonFormat[EmployeeData]                  = jsonFormat2(EmployeeData)
  implicit val serviceResponseGetEmployeeJF: RootJsonFormat[ServiceResponse] = jsonFormat1(ServiceResponse)
}

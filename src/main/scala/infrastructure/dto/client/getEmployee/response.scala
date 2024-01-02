package infrastructure.dto.client.getEmployee

import spray.json.DefaultJsonProtocol.{StringJsonFormat, immSeqFormat, jsonFormat1, jsonFormat2, jsonFormat5}
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

case class ServiceResponse(
  result: Result,
  metadata: Metadata
)

object ServiceResponseJsonFormatter {

  import infrastructure.dto.client.CommonJsonFormats._

  implicit val metadataJF: RootJsonFormat[Metadata] = jsonFormat1(Metadata)
  implicit val resultJF: RootJsonFormat[Result] = jsonFormat5(Result)
  implicit val serviceResponseGetEmployeeJF: RootJsonFormat[ServiceResponse] = jsonFormat2(ServiceResponse)
}

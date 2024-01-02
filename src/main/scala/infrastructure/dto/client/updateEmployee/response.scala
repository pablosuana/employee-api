package infrastructure.dto.client.updateEmployee

import spray.json.DefaultJsonProtocol.{StringJsonFormat, jsonFormat4}
import spray.json.RootJsonFormat

import java.util.UUID

case class ServiceResponse(
  id: UUID = UUID.randomUUID(),
  email: String,
  status: String,
  updated_at: String
)

object ServiceResponseJsonFormatter {

  import infrastructure.dto.client.CommonJsonFormats._

  implicit val serviceResponseJF: RootJsonFormat[ServiceResponse] = jsonFormat4(ServiceResponse)
}

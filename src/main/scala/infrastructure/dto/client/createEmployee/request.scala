package infrastructure.dto.client.createEmployee

import spray.json.RootJsonFormat

import java.util.UUID

case class ServiceRequest(
  id: UUID = UUID.randomUUID(),
  email: String,
  full_name: String,
  date_of_birth: String,
  hobbies: Seq[String]
)

object ServiceRequestJsonFormatter {

  import infrastructure.dto.client.CommonJsonFormats._
  import spray.json.DefaultJsonProtocol._

  implicit val serviceRequestJF: RootJsonFormat[ServiceRequest] = jsonFormat5(ServiceRequest)
}

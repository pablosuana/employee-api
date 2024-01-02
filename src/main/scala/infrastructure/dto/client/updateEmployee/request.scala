package infrastructure.dto.client.updateEmployee

import spray.json.RootJsonFormat

import java.util.UUID
// TODO improve query to not need to pass all of the fields. Query needs to be updated
case class ServiceRequest(
  id: UUID = UUID.randomUUID(),
  email: String,
  full_name: Option[String],
  date_of_birth: Option[String],
  hobbies: Seq[String]
)

object ServiceRequestJsonFormatter {

  import infrastructure.dto.client.CommonJsonFormats._
  import spray.json.DefaultJsonProtocol._

  implicit val serviceRequestJF: RootJsonFormat[ServiceRequest] = jsonFormat5(ServiceRequest)
}

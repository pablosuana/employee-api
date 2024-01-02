package infrastructure.dto.client.createEmployee

import spray.json.RootJsonFormat

case class ServiceRequest(
  email: String,
  full_name: String,
  date_of_birth: String,
  hobbies: Seq[String]
)

object ServiceRequestJsonFormatter {

  import infrastructure.dto.client.CommonJsonFormats._
  import spray.json.DefaultJsonProtocol._

  implicit val serviceRequestJF: RootJsonFormat[ServiceRequest] = jsonFormat4(ServiceRequest)
}

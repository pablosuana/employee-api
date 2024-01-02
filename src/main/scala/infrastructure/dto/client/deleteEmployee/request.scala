package infrastructure.dto.client.deleteEmployee

import spray.json.RootJsonFormat

import java.util.UUID

case class ServiceRequest(
  id: Option[UUID],
  email: Option[String]
)

object ServiceRequestJsonFormatter {

  import infrastructure.dto.client.CommonJsonFormats._
  import spray.json.DefaultJsonProtocol._

  implicit val serviceRequestDeleteEmployeeJF: RootJsonFormat[ServiceRequest] = jsonFormat2(ServiceRequest)
}

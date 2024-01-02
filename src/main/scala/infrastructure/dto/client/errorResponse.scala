package infrastructure.dto.client

import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

case class ErrorResponse(code: Int, message: String)

object ErrorResponseJsonFormat {
  implicit val errorResponseJsonFormat: RootJsonFormat[ErrorResponse] = jsonFormat2(ErrorResponse)
}

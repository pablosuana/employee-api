package infrastructure.dto.client

import spray.json.DefaultJsonProtocol._

case class ErrorResponse(code: Int, message: String)

object ErrorResponseJsonFormat {
  implicit val errorResponseJsonFormat = jsonFormat2(ErrorResponse)
}

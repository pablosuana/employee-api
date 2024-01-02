package infrastructure.dto.client

import spray.json.{JsString, JsValue, JsonFormat, deserializationError}

import java.util.UUID

object CommonJsonFormats {

  implicit val uuidFormat: JsonFormat[UUID] = new JsonFormat[UUID] {
    def write(u: UUID): JsString = JsString(u.toString)

    def read(value: JsValue): UUID = value match {
      case JsString(v) => UUID.fromString(v)
      case other       => deserializationError(s"A string representing UUID is expected but was $other")
    }
  }

}

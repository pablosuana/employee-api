package infrastructure.dto.db

import com.domain.interfaces.db.DbResponse
import spray.json.DefaultJsonProtocol.{StringJsonFormat, jsonFormat7}
import spray.json.RootJsonFormat

case class PostgresResponse(
  id: String,
  full_name: String,
  email: String,
  date_of_birth: String,
  hobbies: String,
  created_at: String,
  updated_at: String
) extends DbResponse

object PostgresResponseJsonFormat {

  implicit val postgresResponseJsonFormat: RootJsonFormat[PostgresResponse] = jsonFormat7(PostgresResponse)
}

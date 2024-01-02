package com.employee.infrastructure.db

import com.employee.domain.interfaces.db.{CreateQuery, DeleteQuery, GetQuery, UpdateQuery}
import com.employee.infrastructure.dto.db.PostgresRequest

import java.util.UUID

case class GetOnePostgresQuery(id: Option[UUID], email: Option[String], tableName: String) extends GetQuery[String] {

  override def queryMapper: String =
    if (id.nonEmpty) s"""SELECT * from $tableName where id = '${id.get}' ORDER BY updated_at DESC limit 1""".stripMargin
    else if (email.nonEmpty) s"""SELECT * from $tableName where email = '${email.get}' ORDER BY updated_at DESC limit 1""".stripMargin
    else throw new MatchError("Either id or email has to be provided")
}

case class GetAllPostgresQuery(tableName: String) extends GetQuery[String] {

  override def queryMapper: String = s"SELECT * from $tableName".stripMargin
}

case class CreatePostgresQuery(postgresRequest: PostgresRequest, tableName: String) extends CreateQuery[String] {

  override def queryMapper: String =
    s"""
         |INSERT INTO $tableName (id, full_name, email, date_of_birth, hobbies, created_at, updated_at)
         |VALUES ('${postgresRequest.id}', '${postgresRequest.full_name}', '${postgresRequest.email}', '${postgresRequest.date_of_birth}', '${postgresRequest.hobbies}', '${postgresRequest.timestamp}', '${postgresRequest.timestamp}');
         |""".stripMargin
}

case class DeletePostgresQuery(id: Option[UUID], email: Option[String], tableName: String) extends DeleteQuery[String] {

  override def queryMapper: String =
    if (id.nonEmpty) s"""DELETE FROM $tableName where id = '${id.get}';""".stripMargin
    else if (email.nonEmpty) s"""DELETE FROM $tableName where email = '${email.get}';""".stripMargin
    else throw new MatchError("Either id or email has to be provided")
}

case class UpdatePostgresQuery(postgresRequest: PostgresRequest, tableName: String) extends UpdateQuery[String] {

  override def queryMapper: String =
      s"""UPDATE $tableName
         |SET full_name='${postgresRequest.full_name}',
         |email='${postgresRequest.email}', date_of_birth='${postgresRequest.date_of_birth}',
         | hobbies='${postgresRequest.hobbies}', updated_at='${postgresRequest.timestamp}'
         | WHERE id = '${postgresRequest.id}';
         """.stripMargin
}

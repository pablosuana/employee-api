package utils

import com.domain.{Employee, EmployeeBase}
import interfaces.{CreateQuery, DeleteQuery, GetQuery, UpdateQuery}

object GetOnePostgresQuery extends GetQuery[String] {

  override val tableName: String = "test_table"

  override def queryMapper(employeeBase: EmployeeBase): String = employeeBase match {
    case Employee(id, _, _, _, _) =>
      s"""SELECT * from $tableName where id = $id""".stripMargin
    case Employee(_, email, _, _, _) =>
      s"""SELECT * from $tableName where email = $email""".stripMargin
    case _ => throw new MatchError("The only supported subtype of EmployeeBase is Employee, this should not be reached")
  }
}

object GetAllPostgresQuery extends GetQuery[String] {
  override val tableName: String = "test_table"

  override def queryMapper(employeeBase: EmployeeBase): String = s"SELECT * from $tableName".stripMargin
}

object CreatePostgresQuery extends CreateQuery[String] {
  override val tableName: String = "test_table"

  override def queryMapper(employeeBase: EmployeeBase): String = employeeBase match {
    case Employee(id, email, fullName, dateOfBirth, hobbies) =>
      s"""
         |INSERT INTO $tableName ("id", "full_name", "email", "date_of_birth", "hobbies")
         |VALUES ('$id', '$fullName', '$email', $dateOfBirth, '$hobbies');
         |""".stripMargin
    case _ => throw new MatchError("The only supported subtype of EmployeeBase is Employee, this should not be reached")
  }
}

object DeletePostgresQuery extends DeleteQuery[String] {
  override val tableName: String = "test_table"

  override def queryMapper(employeeBase: EmployeeBase): String = employeeBase match {
    case Employee(id, _, _, _, _) =>
      s"""DELETE FROM $tableName where id = $id;""".stripMargin
    case _ => throw new MatchError("The only supported subtype of EmployeeBase is Employee, this should not be reached")
  }
}

object UpdatePostgresQuery extends UpdateQuery[String] {
  override val tableName: String = "test_table"

  override def queryMapper(employeeBase: EmployeeBase): String = employeeBase match {
    case Employee(id, email, fullName, dateOfBirth, hobbies) =>
      s"""UPDATE $tableName
         |SET id=$id, full_name=$fullName, email=$email, date_of_birth=$dateOfBirth, hobbies=$hobbies
         |WHERE email = $email;
         """.stripMargin
    case _ => throw new MatchError("The only supported subtype of EmployeeBase is Employee, this should not be reached")
  }
}

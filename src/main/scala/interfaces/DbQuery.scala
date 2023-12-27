package interfaces

import com.domain.EmployeeBase

sealed trait DbQuery[QueryToDb] {
  val tableName: String
  def queryMapper(employeeBase: EmployeeBase): QueryToDb
}

abstract class GetQuery[QueryToDb]    extends DbQuery[QueryToDb]
abstract class CreateQuery[QueryToDb] extends DbQuery[QueryToDb]
abstract class DeleteQuery[QueryToDb] extends DbQuery[QueryToDb]
abstract class UpdateQuery[QueryToDb] extends DbQuery[QueryToDb]

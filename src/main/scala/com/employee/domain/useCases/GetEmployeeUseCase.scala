package com.employee.domain.useCases

import com.employee.domain.interfaces.EmployeeRepository
import com.employee.domain.interfaces.db.DbResponse

import java.util.UUID
import scala.concurrent.Future

case class GetEmployeeUseCase[DbResponseType <: DbResponse](
  employeeRepository: EmployeeRepository[DbResponseType, _]
) {
  def getEmployee(id: String): Future[Option[DbResponseType]] =
      employeeRepository.getEmployeeById(UUID.fromString(id))
}

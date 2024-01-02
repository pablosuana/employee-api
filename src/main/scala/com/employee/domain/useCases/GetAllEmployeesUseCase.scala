package com.employee.domain.useCases

import com.employee.domain.interfaces.EmployeeRepository
import com.employee.domain.interfaces.db.DbResponse

import scala.concurrent.Future

case class GetAllEmployeesUseCase[DbResponseType <: DbResponse](
  employeeRepository: EmployeeRepository[DbResponseType, _]
) {
  def getAllEmployees: Future[Seq[DbResponseType]] =
      employeeRepository.getAllEmployees
}

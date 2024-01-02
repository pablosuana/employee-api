package com.domain.useCases

import com.domain.interfaces.EmployeeRepository
import com.domain.interfaces.db.DbResponse

import scala.concurrent.Future

case class GetAllEmployeesUseCase[DbResponseType <: DbResponse](
  employeeRepository: EmployeeRepository[DbResponseType, _]
) {
  def getAllEmployees: Future[Seq[DbResponseType]] =
      employeeRepository.getAllEmployees
}

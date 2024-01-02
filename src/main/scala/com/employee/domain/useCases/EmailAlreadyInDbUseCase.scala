package com.employee.domain.useCases

import com.employee.domain.interfaces.EmployeeRepository
import com.employee.domain.interfaces.db.DbResponse

import scala.concurrent.Future

case class EmailAlreadyInDbUseCase[DbResponseType <: DbResponse](
  employeeRepository: EmployeeRepository[DbResponseType, _]
) {
  def isEmailInDb(email: String): Future[Option[DbResponseType]] =
    employeeRepository.isEmailInDb(email)
}

package com.domain.useCases

import com.domain.Entities.EmployeeBase
import com.domain.interfaces.EmployeeRepository

import scala.concurrent.Future

case class DeleteEmployeeUseCase[EmployeeType <: EmployeeBase](employeeRepository: EmployeeRepository[_, EmployeeType]) {
  def deleteEmployee(email: Option[String], id: Option[String]): Future[Boolean] = {
    employeeRepository.deleteEmployee(email, id)
  }
}

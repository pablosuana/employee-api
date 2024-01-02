package com.employee.domain.useCases

import com.employee.domain.entities.EmployeeBase
import com.employee.domain.interfaces.EmployeeRepository

import scala.concurrent.Future

case class UpdateEmployeeUseCase[EmployeeType <: EmployeeBase](employeeRepository: EmployeeRepository[_, EmployeeType]) {
  def updateEmployee(employee: EmployeeType, idToUpdate: String, updatedAt: String): Future[Boolean] = {
    employeeRepository.updateEmployee(employee, idToUpdate, updatedAt)
  }
}

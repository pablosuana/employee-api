package com.domain.useCases

import com.domain.Entities.EmployeeBase
import com.domain.interfaces.EmployeeRepository

import scala.concurrent.Future

case class UpdateEmployeeUseCase[EmployeeType <: EmployeeBase](employeeRepository: EmployeeRepository[_, EmployeeType]) {
  def updateEmployee(employee: EmployeeType, idToUpdate: String, updatedAt: String): Future[Boolean] = {
    employeeRepository.updateEmployee(employee, idToUpdate, updatedAt)
  }
}

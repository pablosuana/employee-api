package com.employee.domain.useCases

import com.employee.domain.entities.EmployeeBase
import com.employee.domain.interfaces.EmployeeRepository

import scala.concurrent.Future

case class CreateEmployeeUseCase[EmployeeType <: EmployeeBase](employeeRepository: EmployeeRepository[_, EmployeeType]) {
  def createEmployee(employee: EmployeeType, createdAt: String): Future[Boolean] = {
    employeeRepository.createEmployee(employee, createdAt)
  }
}

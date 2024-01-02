package com.domain.useCases

import com.domain.Entities.EmployeeBase
import com.domain.interfaces.EmployeeRepository

import scala.concurrent.Future

case class CreateEmployeeUseCase[EmployeeType <: EmployeeBase](employeeRepository: EmployeeRepository[_, EmployeeType]) {
  def createEmployee(employee: EmployeeType, createdAt: String): Future[Boolean] = {
    employeeRepository.createEmployee(employee, createdAt)
  }
}

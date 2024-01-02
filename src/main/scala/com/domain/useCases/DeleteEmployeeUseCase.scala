package com.domain.useCases

import com.domain.interfaces.EmployeeRepository

import scala.concurrent.Future

case class DeleteEmployeeUseCase(employeeRepository: EmployeeRepository[_, _]) {
  def deleteEmployee(email: Option[String], id: Option[String]): Future[Boolean] = {
    employeeRepository.deleteEmployee(email, id)
  }
}

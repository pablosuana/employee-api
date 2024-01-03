package com.employee.domain.useCases

import com.employee.domain.interfaces.EmployeeRepository

import java.util.UUID
import scala.concurrent.Future

case class DeleteEmployeeUseCase(employeeRepository: EmployeeRepository[_, _]) {
  def deleteEmployee(email: Option[String], id: Option[UUID]): Future[Boolean] = {
    employeeRepository.deleteEmployee(email, id)
  }
}

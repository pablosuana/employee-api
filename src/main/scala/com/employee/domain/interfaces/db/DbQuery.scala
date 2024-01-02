package com.employee.domain.interfaces.db

sealed trait DbQuery[QueryToDb] {
  def queryMapper: QueryToDb
}

abstract class GetQuery[QueryToDb]    extends DbQuery[QueryToDb]
abstract class CreateQuery[QueryToDb] extends DbQuery[QueryToDb]
abstract class DeleteQuery[QueryToDb] extends DbQuery[QueryToDb]
abstract class UpdateQuery[QueryToDb] extends DbQuery[QueryToDb]

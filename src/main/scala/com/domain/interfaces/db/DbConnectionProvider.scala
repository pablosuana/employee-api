package com.domain.interfaces.db

trait DbConnectionProvider[ConnectionType] {
  val dbConfig: DbConfigBase
  val tableName: String
  val getConnection: ConnectionType
}

trait DbConfigBase

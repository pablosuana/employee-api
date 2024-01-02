package com.employee.infrastructure.db

import com.employee.domain.interfaces.db.DbConfigBase
import com.typesafe.config.ConfigFactory

class PostgresInMemoryConnectionConf(
  val host: String,
  val port: Int,
  val database: String,
  val username: String,
  val password: String,
  val tableName: String
) extends DbConfigBase

object PostgresInMemoryConnectionConf {
  def apply(configFileName: String): PostgresInMemoryConnectionConf = {
    val config = ConfigFactory.load(configFileName).getConfig("async-cache-db").getConfig("database")
    new PostgresInMemoryConnectionConf(
      host = config.getString("host"),
      port = config.getInt("port"),
      database = config.getString("database"),
      username = config.getString("username"),
      password = config.getString("password"),
      tableName = config.getString("table-name")
    )
  }
}

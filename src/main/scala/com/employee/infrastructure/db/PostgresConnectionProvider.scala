package com.employee.infrastructure.db

import com.employee.domain.interfaces.db.DbConnectionProvider
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.slf4j.LoggerFactory

import java.sql.Connection

class PostgresConnectionProvider(override val dbConfig: PostgresInMemoryConnectionConf) extends DbConnectionProvider[Connection] {
  private val logger           = LoggerFactory.getLogger(getClass)

  private val hikariConfig: HikariConfig = {
    val hc = new HikariConfig()
    hc.setJdbcUrl(s"${dbConfig.host}:${dbConfig.port}/${dbConfig.database}")
    hc.setUsername(s"${dbConfig.username}")
    hc.setPassword(s"${dbConfig.password}")
    logger.info(s"Hikari conf: $hc")
    hc
  }
  val tableName: String         = dbConfig.tableName
  val getConnection: Connection = new HikariDataSource(hikariConfig).getConnection()
}
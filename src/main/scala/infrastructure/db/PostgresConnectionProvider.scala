package infrastructure.db

import com.domain.interfaces.db.DbConnectionProvider
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

import java.sql.Connection

class PostgresConnectionProvider(override val dbConfig: PostgresInMemoryConnectionConf) extends DbConnectionProvider[Connection] {

  private val hikariConfig: HikariConfig = {
    val hc = new HikariConfig()
    hc.setJdbcUrl(s"${dbConfig.host}:${dbConfig.port}/${dbConfig.database}")
    hc.setUsername(s"${dbConfig.username}")
    hc.setPassword(s"${dbConfig.password}")
    hc
  }
  val tableName: String         = dbConfig.tableName
  val getConnection: Connection = new HikariDataSource(hikariConfig).getConnection()
}
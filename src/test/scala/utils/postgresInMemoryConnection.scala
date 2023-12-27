package utils

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import interfaces.{DbConfigBase, DbConnectionProvider}

import java.sql.Connection


class PostgresInMemoryConnectionProviderProvider(override val dbConfig: PostgresInMemoryConnectionConf) extends DbConnectionProvider[Connection] {

  private val hikariConfig: HikariConfig = {
    val hc = new HikariConfig()
    hc.setJdbcUrl(s"${dbConfig.host}/${dbConfig.database}")
    hc.setUsername("sa")
    hc.setPassword("")
    hc.setMaximumPoolSize(10)
    hc
  }

  val getConnection: Connection = new HikariDataSource(hikariConfig).getConnection()
}

class PostgresInMemoryConnectionConf(
  val host: String,
  val database: String,
  val username: String,
  val password: String,
  val maxActiveConnections: Int
) extends DbConfigBase

object PostgresInMemoryConnectionConf {
  def apply(configFileName: String): PostgresInMemoryConnectionConf = {
    val config = ConfigFactory.load(configFileName).getConfig("async-cache-db").getConfig("database")
    new PostgresInMemoryConnectionConf(
      host = config.getString("host"),
      database = config.getString("database"),
      username = config.getString("username"),
      password = config.getString("password"),
      maxActiveConnections = config.getInt("max-active-connections")
    )
  }
}

package org.example.postgresqlcdcevent.config

import io.debezium.embedded.EmbeddedEngine
import org.example.postgresqlcdcevent.handler.ChangeEventHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File

@Configuration
class DebeziumConfig(
  @Value("\${debezium.database.hostname}") private val dbHostname: String,
  @Value("\${debezium.database.port}") private val dbPort: Int,
  @Value("\${debezium.database.user}") private val dbUsername: String,
  @Value("\${debezium.database.password}") private val dbPassword: String,
  @Value("\${debezium.database.dbname}") private val dbDatabaseName: String
) {
  @Bean
  fun debeziumConfiguration(): io.debezium.config.Configuration {
    // 프로젝트 루트 디렉토리에 offset 파일을 저장하도록 설정
    val offsetStorePath = System.getProperty("user.dir") + File.separator + "offsets.dat"

    return io.debezium.config.Configuration.create()
      .with("name", "postgres-connector")
      .with("connector.class", "io.debezium.connector.postgresql.PostgresConnector")
      .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
      .with("offset.storage.file.filename", offsetStorePath)
      .with("offset.flush.interval.ms", "60000")
      .with("database.hostname", dbHostname)
      .with("database.port", dbPort)
      .with("database.user", dbUsername)
      .with("database.password", dbPassword)
      .with("database.dbname", dbDatabaseName)
      .with("topic.prefix", "postgres")
      .with("database.server.name", "postgres-cdc-server")
      .with("schema.include.list", "public")
      .with("table.include.list", "public.products")
      .with("plugin.name", "pgoutput")
      .build()
  }
}

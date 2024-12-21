package org.example.postgresqlcdcevent.connector

import io.debezium.config.Configuration
import io.debezium.connector.postgresql.ChangeEvent
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.example.postgresqlcdcevent.handler.ChangeEventHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.Executors

@Component
class PostgresConnector(
  private val configuration: Configuration,
  private val changeEventHandler: ChangeEventHandler
) {
  private val logger = LoggerFactory.getLogger(javaClass)
  private val engine = io.debezium.engine.DebeziumEngine.create(io.debezium.engine.format.Json::class.java)
    .using(configuration.asProperties())
    .notifying { record ->
      changeEventHandler.handleEvent(record)
    }
    .build()

  @PostConstruct
  fun start() {
    logger.info("Starting Debezium engine...")
    Executors.newSingleThreadExecutor().execute(engine)
  }

  @PreDestroy
  fun stop() {
    logger.info("Stopping Debezium engine...")
    engine.close()
  }
}

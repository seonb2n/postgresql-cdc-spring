package org.example.postgresqlcdcevent

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PostgresqlCdcEventApplication

fun main(args: Array<String>) {
  runApplication<PostgresqlCdcEventApplication>(*args)
}

package org.example.postgresqlcdcevent.handler

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.debezium.engine.ChangeEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ChangeEventHandler(
  @Value("\${stock.threshold}") private val stockThreshold: Int,
) {
  private val logger = LoggerFactory.getLogger(javaClass)
  private val objectMapper = ObjectMapper()

  fun handleEvent(event: ChangeEvent<String, String>) {
    val value = event.value()
    if (value != null) {
      try {
        val changeEvent = objectMapper.readTree(value)
        logger.info("Received event: $changeEvent")
        processProductChange(changeEvent)
      } catch (e: Exception) {
        logger.error("Error processing change event", e)
      }
    }
  }

  private fun processProductChange(changeEvent: JsonNode) {
    // payload 필드 확인
    val payload = changeEvent.get("payload")
    if (payload == null) {
      logger.error("No payload in change event")
      return
    }

    val operation = payload.get("op")?.asText()
    if (operation == null) {
      logger.error("No operation type in payload")
      return
    }

    when (operation) {
      "u", "c" -> { // update or create
        val after = payload.get("after")
        if (after != null) {
          val productId = after.get("id")?.asLong()
          val productName = after.get("name")?.asText()
          val currentStock = after.get("stock")?.asInt()

          if (productId != null && productName != null && currentStock != null) {
            if (currentStock <= stockThreshold) {
              logger.warn("⚠️ 재고 부족 알림! 상품: $productName (ID: $productId) - 현재 재고: $currentStock")
            }
          }
        }
      }
      "d" -> { // delete
        val before = payload.get("before")
        if (before != null) {
          val productId = before.get("id")?.asLong()
          if (productId != null) {
            logger.info("상품 삭제됨 - ID: $productId")
          }
        }
      }
    }
  }
}

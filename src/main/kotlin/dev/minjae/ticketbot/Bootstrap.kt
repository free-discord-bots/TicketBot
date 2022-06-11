package dev.minjae.ticketbot

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.filter.ThresholdFilter
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dev.minjae.ticketbot.config.BotConfig
import io.sentry.SentryOptions
import io.sentry.logback.SentryAppender
import org.slf4j.LoggerFactory
import java.io.File

fun main() {
    val config: BotConfig = jsonMapper().readValue(
        File("config.json").apply {
            if (!exists()) {
                createNewFile()
                appendBytes(javaClass.getResourceAsStream("/config.json")!!.readAllBytes())
            }
        }.inputStream()
    )
    val rootLogger: Logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
    rootLogger.level = if (config.debug) Level.DEBUG else Level.INFO

    if (config.sentryDSN.isNotEmpty()) {
        rootLogger.info("Initializing Sentry...")
        val context: LoggerContext = rootLogger.loggerContext
        val appender = SentryAppender()

        appender.setOptions(
            SentryOptions().apply {
                dsn = config.sentryDSN
            }
        )
        appender.context = context

        val levelFilter = ThresholdFilter()
        levelFilter.setLevel(Level.ERROR.toString())
        levelFilter.start()

        appender.addFilter(levelFilter)
        appender.start()
        rootLogger.addAppender(appender)

        rootLogger.info("Initialized Sentry!")
    } else {
        rootLogger.info("Sentry DSN is not set. Sentry support will not be enabled.")
    }
}

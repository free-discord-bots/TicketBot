package dev.minjae.ticketbot.config

data class BotConfig(
    val ownerId: Long,
    val token: String,
    val debug: Boolean,
    val sentryDSN: String,
    val mySQLConfig: MySQLConfig
)

package dev.minjae.ticketbot.config

import com.fasterxml.jackson.annotation.JsonAlias

data class MySQLConfig(
    val host: String,
    val port: Int,
    val username: String,
    val password: String,
    @JsonAlias("database") val schema: String
)

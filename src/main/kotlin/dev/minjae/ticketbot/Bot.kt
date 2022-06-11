package dev.minjae.ticketbot

import dev.minjae.ticketbot.config.BotConfig
import dev.minn.jda.ktx.jdabuilder.default

class Bot(val config: BotConfig) {

    val jda = default(config.token) {
        addEventListeners(JDAListener(this@Bot))
    }
}

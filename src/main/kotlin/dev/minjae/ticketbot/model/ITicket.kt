package dev.minjae.ticketbot.model

import io.requery.Column
import io.requery.Entity
import io.requery.Key
import io.requery.Persistable

@Entity
interface ITicket : Persistable {
    @get:Key
    @get:Column(nullable = false)
    val id: Long

    @get:Column(nullable = false)
    val channelId: Long

    @get:Key
    @get:Column(nullable = false)
    val ownerId: Long
}

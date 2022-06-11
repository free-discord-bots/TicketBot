package dev.minjae.ticketbot.model

import io.requery.Column
import io.requery.Entity
import io.requery.Key
import io.requery.Persistable

@Entity
interface ISupportTeam : Persistable {

    @get:Key
    @get:Column(nullable = false)
    val id: Long
}

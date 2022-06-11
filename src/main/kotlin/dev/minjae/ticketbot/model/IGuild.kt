package dev.minjae.ticketbot.model

import io.requery.Column
import io.requery.Entity
import io.requery.Key
import io.requery.ManyToMany
import io.requery.Persistable
import io.requery.Table

@Entity
@Table(name = "ticket")
interface IGuild : Persistable {

    @get:Key
    @get:Column(nullable = false)
    val id: Long

    @get:Column(nullable = false)
    @get:ManyToMany
    val panels: MutableList<IPanel>
}

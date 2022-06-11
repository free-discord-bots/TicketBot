package dev.minjae.ticketbot.model

import io.requery.Column
import io.requery.Entity
import io.requery.Key
import io.requery.ManyToMany
import io.requery.Persistable

@Entity
interface IPanel : Persistable {

    @get:Key
    @get:Column(nullable = false)
    val name: String

    @get:Column(nullable = false)
    val description: String

    @get:Column(nullable = false)
    val welcomeMessage: String

    @get:Column(nullable = false)
    val categoryId: Long

    @get:Column(nullable = false)
    @get:ManyToMany
    val tickets: MutableList<ITicket>

    @get:Column(nullable = false)
    @get:ManyToMany
    val supportTeams: MutableList<ISupportTeam>

    @get:Column(nullable = false)
    @get:ManyToMany
    val blackList: MutableList<IBlackList>
}

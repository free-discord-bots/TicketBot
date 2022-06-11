package dev.minjae.ticketbot.cache

import dev.minjae.ticketbot.Bot
import dev.minjae.ticketbot.model.IGuild
import dev.minjae.ticketbot.model.IPanel
import dev.minjae.ticketbot.model.ITicket
import dev.minjae.ticketbot.model.Ticket
import dev.minn.jda.ktx.interactions.components.button
import dev.minn.jda.ktx.messages.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

data class GuildCache(
    val bot: Bot,
    val id: Long,
    val guild: IGuild
) {
    fun getPanel(name: String): IPanel? = guild.panels.firstOrNull { it.name == name }

    fun getPanels(): MutableList<IPanel> = guild.panels

    fun getTicket(panel: IPanel, id: Long): ITicket? = panel.tickets.firstOrNull { it.id == id }

    @Synchronized
    fun createTicket(member: Member, panel: IPanel) {
        // TODO: Check if member is blacklisted
        val category = member.guild.getCategoryById(panel.categoryId) ?: return
        val action = category.createTextChannel("ticket-${panel.name}-${panel.tickets.size}")
            .addPermissionOverride(member.guild.publicRole, emptySet(), setOf(Permission.VIEW_CHANNEL))
            .addMemberPermissionOverride(member.idLong, setOf(Permission.VIEW_CHANNEL), emptySet())

        panel.supportTeams.forEach {
            action.addRolePermissionOverride(it.id, setOf(Permission.VIEW_CHANNEL), emptySet())
        }
        action.queue({ channel ->
            val ticket = Ticket().apply {
                setId(panel.tickets.size.toLong())
                setOwnerId(member.idLong)
                setChannelId(channel.idLong)
            }
            panel.tickets.add(ticket)
            // TODO: Update panel data

            val close = bot.jda.button(label = "Close Ticket", style = ButtonStyle.DANGER) {
                // TODO: Close ticket
                val currentTicket = getTicketByChannelId(it.channel.idLong)
                if (currentTicket != null) {
                    sendConfirmMessage(it.textChannel)
                }
            }
            channel.sendMessage(panel.welcomeMessage)
                .setActionRow(close)
                .queue({ message ->
                    message.editMessageEmbeds(
                        EmbedBuilder {
                        }.build()
                    ).queue(null) {}
                }) {}
        }) {}
    }

    @Synchronized
    fun closeTicket(ticket: ITicket) {
        val channel = bot.jda.getGuildChannelById(ticket.channelId) ?: return
        // TODO: Transcript
        getPanelByTicket(ticket)?.let { panel ->
            panel.tickets.remove(ticket)
            // TODO: Update panel data
        }
        channel.delete().reason("Closed Ticket").queue(null) {}
    }

    fun getTicketByChannelId(channelId: Long): ITicket? =
        guild.panels.flatMap { it.tickets }.firstOrNull { it.channelId == channelId }

    fun getPanelByTicket(ticket: ITicket): IPanel? =
        guild.panels.firstOrNull { it.tickets.contains(ticket) }

    fun sendConfirmMessage(channel: TextChannel) {
        val confirm = bot.jda.button(label = "Confirm", style = ButtonStyle.DANGER) {
            val currentTicket = getTicketByChannelId(it.channel.idLong)
            if (currentTicket != null) {
                threadPool.schedule({ closeTicket(currentTicket) }, 3L, TimeUnit.SECONDS)
            }
        }
        channel.sendMessageEmbeds(
            EmbedBuilder {
                title = "Close Ticket Confirm"
                description = "Are you sure to close ticket?"
            }.build()
        ).setActionRow(confirm)
            .queue(null) {
            }
    }

    companion object {
        val threadPool: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    }
}

package net.kunmc.lab.netherblockconverter.events

import net.kunmc.lab.netherblockconverter.Config
import net.kunmc.lab.netherblockconverter.game.GameManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.PortalCreateEvent


class EventListener: Listener {
    @EventHandler
    fun onCreateNehterGate(e: PortalCreateEvent ) {
        if (e.entity !is Player) return

        if (Config.isGateAppending) {
            GameManager.converterPlayers.add(e.entity!!.uniqueId)
        }
    }
}
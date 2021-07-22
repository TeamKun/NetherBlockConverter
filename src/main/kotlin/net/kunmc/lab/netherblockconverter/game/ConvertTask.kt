package net.kunmc.lab.netherblockconverter.game

import net.kunmc.lab.netherblockconverter.logic.BlockConverter
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.scheduler.BukkitRunnable

class ConvertTask : BukkitRunnable() {
    override fun run() {
        Bukkit.getOnlinePlayers().forEach lit@{
            var p = it.player
            if (p?.gameMode == GameMode.CREATIVE || p?.gameMode == GameMode.SPECTATOR) return@lit
            if (!GameManager.converterPlayers.contains(p?.uniqueId)) return@lit
            p?.let {
                BlockConverter.convertBlock(p)
            }
        }
    }
}
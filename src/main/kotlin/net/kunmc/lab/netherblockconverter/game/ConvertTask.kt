package net.kunmc.lab.netherblockconverter.game

import net.kunmc.lab.netherblockconverter.Config
import net.kunmc.lab.netherblockconverter.logic.BlockConverter
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getLogger
import org.bukkit.Location
import org.bukkit.scheduler.BukkitRunnable

class ConvertTask: BukkitRunnable() {
    override fun run() {
        Bukkit.getOnlinePlayers().forEach lit@{
            var p = it.player
            if (!GameManager.converterPlayers.contains(p?.uniqueId)) return@lit
            p?.let {
                getLogger().info("AAA")
                BlockConverter.convertBlock(p)
            }
        }
    }
}
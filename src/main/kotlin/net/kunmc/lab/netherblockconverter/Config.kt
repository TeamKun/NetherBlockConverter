package net.kunmc.lab.netherblockconverter

import org.bukkit.entity.Player

class Config {
    companion object {
        var range = -1
        var converterPlayers = mutableListOf<Player>()

        fun loadConfig(isReload: Boolean) {
            val plugin = NetherBlockConverter.plugin
            plugin.saveDefaultConfig()
            if (isReload) {
                plugin.reloadConfig()
            }
            var config = plugin.config

            range = config.getInt("range")
        }
    }
}
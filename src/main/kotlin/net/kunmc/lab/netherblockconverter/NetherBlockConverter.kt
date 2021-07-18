package net.kunmc.lab.netherblockconverter

import net.kunmc.lab.netherblockconverter.command.CommandConst
import net.kunmc.lab.netherblockconverter.command.CommandController
import net.kunmc.lab.netherblockconverter.game.TaskManager
import org.bukkit.plugin.java.JavaPlugin

class NetherBlockConverter : JavaPlugin() {
    companion object {
        lateinit var plugin: JavaPlugin
    }

    override fun onEnable() {
        plugin = this
        saveDefaultConfig()
        Config.loadConfig(false)

        getCommand(CommandConst.MAIN_COMMAND)?.setExecutor(CommandController())
        TaskManager.runConvertTask()
        getLogger().info("NetherBlockConverter Plugin is enabled")
    }

    override fun onDisable() {
        Config.loadConfig(true)
        getLogger().info("NetherBlockConverter Plugin is disabled")
    }
}
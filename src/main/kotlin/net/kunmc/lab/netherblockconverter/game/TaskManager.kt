package net.kunmc.lab.netherblockconverter.game

import net.kunmc.lab.netherblockconverter.Config
import net.kunmc.lab.netherblockconverter.NetherBlockConverter
import org.bukkit.scheduler.BukkitTask

class TaskManager {
    companion object {
        var convertTask: BukkitTask? = null

        fun runConvertTask() {
            convertTask?.let {
                convertTask?.cancel();
            }
            convertTask =  ConvertTask().runTaskTimer(NetherBlockConverter.plugin, 0, Config.tick)
        }
    }
}
package net.kunmc.lab.netherblockconverter.logic

import net.kunmc.lab.netherblockconverter.Config
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class BlockConverter {
    companion object {
        fun convertBlock(p: Player){
            if (p.getLocation().getWorld().environment == World.Environment.NORMAL) {
                convertTargetBlock(p, Config.nomalWorldConvertList)
            } else if (p.getLocation().getWorld().environment == World.Environment.NETHER) {
                convertTargetBlock(p, Config.netherWorldConvertList)
            }
       }
        private fun convertTargetBlock(p: Player, convertList: Map<String, Material>) {
            var px = p.getLocation().x
            var py = p.getLocation().y
            var pz = p.getLocation().z

            for (x in Config.range * -1 until Config.range +1) {
                for (y in Config.range * -1 until Config.range +1) {
                    for (z in Config.range * -1 until Config.range + 1) {
                        var loc = Location(p.world, px + x, py + y, pz + z)
                        var currentBlockName = loc.block.type.toString()
                        if (convertList.containsKey(currentBlockName)){
                            var newBlock = convertList[currentBlockName]
                            newBlock?.let {
                                loc.block.type = it
                            }
                        }
                    }
                }
            }
        }
    }
}
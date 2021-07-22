package net.kunmc.lab.netherblockconverter

import org.bukkit.Material

class Config {
    companion object {
        var tick: Long = 1
        var range = 1
        var isGateAppending = false
        var nomalWorldConvertList = mutableMapOf<String, Material>()
        var netherWorldConvertList = mutableMapOf<String, Material>()
        var endWorldConvertList = mutableMapOf<String, Material>()

        fun loadConfig(isReload: Boolean) {
            val plugin = NetherBlockConverter.plugin
            plugin.saveDefaultConfig()
            if (isReload) {
                plugin.reloadConfig()
            }
            var config = plugin.config

            tick = config.getLong("tick")
            range = config.getInt("range")
            isGateAppending = false

            val fileName = "/convertTable.csv"
            val reader = NetherBlockConverter::class.java.getResourceAsStream(fileName)
            nomalWorldConvertList.clear()
            netherWorldConvertList.clear()
            endWorldConvertList.clear()
            reader.bufferedReader().forEachLine {
                var convertSetting = it.split(",")
                if (convertSetting.size < 2) return@forEachLine
                var material = Material.getMaterial(convertSetting[1])

                // 0 nomal -> nether, 1 nether -> normal
                if (convertSetting[2] == "0") {
                    material?.let {
                        nomalWorldConvertList.put(convertSetting[0], material)
                    }
                } else if (convertSetting[2] == "1") {
                    material?.let {
                        netherWorldConvertList.put(convertSetting[0], material)
                    }
                } else if (convertSetting[2] == "2") {
                    material?.let {
                        endWorldConvertList.put(convertSetting[0], material)
                    }
                }
            }

            // 調査用のコード
            //for(mat in Material.values()) {
            //    getLogger().info(mat.name + "," + mat.isAir + "," + mat.isBlock + ","
            //            + mat.isEdible + "," + mat.isBurnable + "," + mat.isEmpty + "," + mat.isFlammable + ","
            //            + mat.isFuel + "," + mat.isOccluding + "," + mat.isInteractable + "," + mat.isItem + ","
            //            + mat.isRecord + "," + mat.isSolid)
            //}
        }
    }
}
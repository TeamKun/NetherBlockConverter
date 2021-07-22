package net.kunmc.lab.netherblockconverter.logic

import net.kunmc.lab.netherblockconverter.Config
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.data.Directional
import org.bukkit.block.data.type.Door
import org.bukkit.block.data.type.Stairs
import org.bukkit.block.data.type.TrapDoor
import org.bukkit.block.data.type.Switch
import org.bukkit.entity.Player
import java.awt.Button

class BlockConverter {
    companion object {
        fun convertBlock(p: Player) {
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
                        var dist = Math.sqrt((x*x + y*y + z*z).toDouble());
                        if (dist > Config.range)
                            continue
                        var currentBlock = Location(p.world, px + x, py + y, pz + z).block
                        val currentBlockName = currentBlock.type.toString()
                        if (convertList.containsKey(currentBlockName)) {
                            /**
                             * ブロックの更新処理
                             *   - 基本的にはtypeを更新するだけだが、階段や松明など配置向きはBlockDataを更新する必要がある
                             */

                            val newMatrial = convertList[currentBlockName] ?: return
                            if (currentBlock.blockData is Stairs) {
                                var newBlockData = newMatrial.createBlockData()
                                (newBlockData as Stairs).facing = (currentBlock.blockData as Stairs).facing
                                newBlockData.half = (currentBlock.blockData as Stairs).half
                                newBlockData.shape = (currentBlock.blockData as Stairs).shape

                                currentBlock.type = newMatrial
                                currentBlock.blockData = newBlockData
                            } else if (currentBlock.type == Material.WALL_TORCH || currentBlock.type == Material.SOUL_WALL_TORCH) {
                                // TORCHはAPIがあまりなさそうなのでcreate時に方向指定をする
                                // https://www.spigotmc.org/threads/how-do-you-place-an-upward-facing-button-with-blockdata.443635/
                                val dir = (currentBlock.blockData as Directional).facing.toString().toLowerCase()
                                var newBlockData = newMatrial.createBlockData("[facing=${dir}]")
                                currentBlock.type = newMatrial
                                currentBlock.blockData = newBlockData
                            } else if (currentBlock.blockData is TrapDoor) {
                                var newBlockData = newMatrial.createBlockData()
                                (newBlockData as TrapDoor).facing = (currentBlock.blockData as TrapDoor).facing
                                newBlockData.isOpen = (currentBlock.blockData as TrapDoor).isOpen
                                newBlockData.half = (currentBlock.blockData as TrapDoor).half
                                newBlockData.isPowered = (currentBlock.blockData as TrapDoor).isPowered
                                newBlockData.isWaterlogged = (currentBlock.blockData as TrapDoor).isWaterlogged

                                currentBlock.type = newMatrial
                                currentBlock.blockData = newBlockData
                                //} else if(currentBlock.blockData is Door) {
                                //    if (Location(p.world, px + x, py + y+1.0, pz + z).block.blockData is Door) {
                                //        var anotherBlock = Location(p.world, px + x, py + y+1, pz + z).block
                                //        updateDoor(currentBlock, anotherBlock, convertList)
                                //    } else if (Location(p.world, px + x, py + y-1.0, pz + z).block.blockData is Door) {
                                //        var anotherBlock = Location(p.world, px + x, py + y-1, pz + z).block
                                //        updateDoor(currentBlock, anotherBlock, convertList)
                                //    }
                            } else if (currentBlock.blockData is Switch){
                                var newBlockData = newMatrial.createBlockData()
                                (newBlockData as Switch).facing = (currentBlock.blockData as Switch).facing
                                newBlockData.isPowered = (currentBlock.blockData as Switch).isPowered
                                newBlockData.attachedFace = (currentBlock.blockData as Switch).attachedFace

                                currentBlock.type = newMatrial
                                currentBlock.blockData = newBlockData
                            } else {
                                currentBlock.type = newMatrial
                            }
                        }
                    }
                }
            }
        }
        //private fun updateDoor(block: Block, anotherBlock: Block, convertList: Map<String, Material>){
        //    // Door関連の変換、用件次第で見直して導入
        //    var newBlockMaterial = convertList[block.type.toString()] ?: return
        //    var newAnotherBlockMaterial = convertList[block.type.toString()] ?: return

        //    var newBlockData = newBlockMaterial.createBlockData()
        //    var newAnotherBlockData = newAnotherBlockMaterial.createBlockData()

        //    (newBlockData as Door).facing = (block.blockData as Door).facing
        //    newBlockData.half = (block.blockData as Door).half
        //    newBlockData.isOpen = (block.blockData as Door).isOpen
        //    newBlockData.isPowered = (block.blockData as Door).isPowered
        //    newBlockData.hinge = (block.blockData as Door).hinge

        //    (newAnotherBlockData as Door).facing = (anotherBlock.blockData as Door).facing
        //    newAnotherBlockData.half = (anotherBlock.blockData as Door).half
        //    newAnotherBlockData.isOpen = (anotherBlock.blockData as Door).isOpen
        //    newAnotherBlockData.isPowered = (anotherBlock.blockData as Door).isPowered
        //    newAnotherBlockData.hinge = (anotherBlock.blockData as Door).hinge

        //    block.type = newBlockMaterial
        //    block.blockData = newBlockData
        //    anotherBlock.type = newAnotherBlockMaterial
        //    anotherBlock.blockData = newAnotherBlockData
        //}
    }
}
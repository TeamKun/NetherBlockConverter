package net.kunmc.lab.netherblockconverter.command

import net.kunmc.lab.netherblockconverter.Config
import net.kunmc.lab.netherblockconverter.game.GameManager
import net.kunmc.lab.netherblockconverter.game.TaskManager
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getLogger
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Entity
import org.bukkit.entity.HumanEntity
import java.util.stream.Collectors




class CommandController: CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when {
            args[0] == CommandConst.COMMAND_ADD -> {
                lateinit var entities: MutableList<Entity>
                try {
                    entities = Bukkit.selectEntities(sender, args[1])
                } catch (e: Exception) {
                    sender.sendMessage("" + ChatColor.RED + "存在しないプレイヤー名が入力されました")
                    return true
                }
                if (entities.isEmpty()) {
                    sender.sendMessage("" + ChatColor.RED + "サーバに接続していないプレイヤー名が入力されました")
                    return true
                }
                entities.forEach {
                    if (GameManager.converterPlayers.contains(it.uniqueId)) {
                        sender.sendMessage("" + ChatColor.RED + "追加済みのプレイヤー名が入力されました")
                        return true
                    }
                    GameManager.converterPlayers.add(it.uniqueId)
                    sender.sendMessage("" + ChatColor.GREEN + it.name + "のネザー化を有効化しました")
                    return true
                }
            }
            args[0] == CommandConst.COMMAND_REMOVE -> {
                lateinit var entities: MutableList<Entity>
                try {
                    entities = Bukkit.selectEntities(sender, args[1])
                } catch (e: Exception) {
                    sender.sendMessage("" + ChatColor.RED + "存在しないプレイヤー名が入力されました")
                    return true
                }
                if (entities.isEmpty()) {
                    sender.sendMessage("" + ChatColor.RED + "サーバに接続していないプレイヤー名が入力されました")
                    return true
                }
                var removeTarget: MutableList<Entity> = arrayListOf()
                entities.forEach {
                    if (!GameManager.converterPlayers.contains(it.uniqueId)) {
                        return@forEach
                    }
                    removeTarget.add(it)
                }
                removeTarget.forEach {
                    GameManager.converterPlayers.remove(it.uniqueId)
                    sender.sendMessage("" + ChatColor.GREEN + it.name + "を削除しました")
                }
                return true
            }
            args[0] == CommandConst.COMMAND_GATE_SWITCH -> {
                if (args.size != 2) {
                    sender.sendMessage("" + ChatColor.RED + "引数の数が不正です。コマンドを確認してください。")
                    return true
                }
                Config.isGateAppending = !Config.isGateAppending
                if (Config.isGateAppending) {
                    sender.sendMessage("" + ChatColor.GREEN + "ネザーゲートを開いたプレイヤーはネザー化が有効化されるようになりました")
                } else {
                    sender.sendMessage("" + ChatColor.GREEN + "ネザーゲートを開いてもネザー化は有効にならないようになりました")
                }
                return true
            }

            args[0] == CommandConst.COMMAND_CONFIG_SET -> {
                when {
                    args[1] == CommandConst.COMMAND_CONFIG_TICK -> {
                        try {
                            Config.tick = args[2].toLong()
                            TaskManager.runConvertTask()
                            sender.sendMessage("" + ChatColor.GREEN + "処理間隔が${Config.tick}Tickに更新されました")
                        } catch (e: NumberFormatException) {
                            sender.sendMessage("" + ChatColor.RED + "不正な入力です。整数を入力してください")
                        }
                        return true
                    }
                    args[1] == CommandConst.COMMAND_CONFIG_RANGE -> {
                        try {
                            Config.range = args[2].toInt()
                            sender.sendMessage("" + ChatColor.GREEN + "有効範囲が${Config.range}に更新されました")
                        } catch (e: NumberFormatException) {
                            sender.sendMessage("" + ChatColor.RED + "不正な入力です。整数を入力してください")
                        }
                        return true
                    }
                }
            }
        }
        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        var completions: MutableList<String> = mutableListOf()
        getLogger().info(args.size.toString())
        when (args.size) {
            1 -> {
                completions.addAll(listOf(CommandConst.COMMAND_ADD,
                        CommandConst.COMMAND_REMOVE,
                        CommandConst.COMMAND_GATE_SWITCH,
                        CommandConst.COMMAND_CONFIG_SET))
            }
            2 -> {
                when {
                    args[0] == CommandConst.COMMAND_ADD -> {
                        var completions_tmp: MutableList<String> = mutableListOf()
                        completions_tmp.addAll(listOf("@a", "@p", "@r", "@s", "@e"))
                        completions_tmp.addAll(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()))
                        completions.addAll(completions_tmp.stream().filter{ e -> e.startsWith(args[1]) }
                                .filter{e -> !GameManager.converterPlayers.contains(Bukkit.getPlayer(e)?.uniqueId)}
                                .collect(Collectors.toList()))
                    }
                    args[0] == CommandConst.COMMAND_REMOVE -> {
                        completions.addAll(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).filter{ e -> e.startsWith(args[1]) }
                                .filter{e -> GameManager.converterPlayers.contains(Bukkit.getPlayer(e)?.uniqueId)}
                                .collect(Collectors.toList()))
                    }
                    args[0] == CommandConst.COMMAND_CONFIG_SET -> {
                        completions.addAll(listOf(CommandConst.COMMAND_CONFIG_TICK, CommandConst.COMMAND_CONFIG_RANGE))
                    }
                }
            }
            3 -> {
                when {
                    args[1] == CommandConst.COMMAND_CONFIG_TICK || args[1] == CommandConst.COMMAND_CONFIG_RANGE -> {
                        completions.add("<数字>")
                    }
                }
            }
        }
        return completions
    }
}
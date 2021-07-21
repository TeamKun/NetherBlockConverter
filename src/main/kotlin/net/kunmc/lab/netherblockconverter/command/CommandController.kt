package net.kunmc.lab.netherblockconverter.command

import net.kunmc.lab.netherblockconverter.Config
import net.kunmc.lab.netherblockconverter.game.GameManager
import net.kunmc.lab.netherblockconverter.game.TaskManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
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
                if (args.size != 2) {
                    sender.sendMessage("" + ChatColor.RED + "引数があっていません。コマンドを確認してください。")
                    sendUsage(sender)
                    return true
                }
                lateinit var entities: MutableList<Entity>
                try {
                    entities = Bukkit.selectEntities(sender, args[1])
                } catch (e: Exception) {
                    sender.sendMessage("" + ChatColor.RED + "存在しないプレイヤー名が入力されました。")
                    return true
                }
                if (entities.isEmpty() || args[1] == "@e") {
                    sender.sendMessage("" + ChatColor.RED + "存在しないまたはサーバに接続していないプレイヤー名が入力されました。")
                    return true
                }
                entities.forEach {
                    if (GameManager.converterPlayers.contains(it.uniqueId)) {
                        sender.sendMessage("" + ChatColor.AQUA + "${it.name}は追加済みです")
                    } else {
                        GameManager.converterPlayers.add(it.uniqueId)
                        sender.sendMessage("" + ChatColor.GREEN + "${it.name}のネザー化を有効化しました。")
                    }
                }
                return true
            }
            args[0] == CommandConst.COMMAND_REMOVE -> {
                if (args.size != 2) {
                    sender.sendMessage("" + ChatColor.RED + "引数があっていません。コマンドを確認してください。")
                    sendUsage(sender)
                    return true
                }
                lateinit var entities: MutableList<Entity>
                try {
                    entities = Bukkit.selectEntities(sender, args[1])
                } catch (e: Exception) {
                    sender.sendMessage("" + ChatColor.RED + "存在しないプレイヤー名が入力されました。")
                    return true
                }
                if (entities.isEmpty() || args[1] == "@e") {
                    sender.sendMessage("" + ChatColor.RED + "存在しないまたはサーバに接続していないプレイヤー名が入力されました。")
                    return true
                }
                var removeTarget: MutableList<Entity> = arrayListOf()
                entities.forEach {
                    if (GameManager.converterPlayers.contains(it.uniqueId)) {
                        removeTarget.add(it)
                    }
                }
                if (removeTarget.size == 0) {
                    sender.sendMessage("" + ChatColor.GREEN + "対象がいませんでした。")
                    return true
                }
                removeTarget.forEach {
                    GameManager.converterPlayers.remove(it.uniqueId)
                    sender.sendMessage("" + ChatColor.GREEN + "${it.name}を削除しました。")
                }
                return true
            }
            args[0] == CommandConst.COMMAND_GATE_SWITCH -> {
                if (args.size != 1) {
                    sender.sendMessage("" + ChatColor.RED + "引数をつけられないコマンドです。コマンドを確認してください。")
                    sendUsage(sender)
                    return true
                }
                Config.isGateAppending = !Config.isGateAppending
                if (Config.isGateAppending) {
                    sender.sendMessage("" + ChatColor.GREEN + "ネザーゲートを開いたプレイヤーはネザー化が有効化されるようになりました。")
                } else {
                    sender.sendMessage("" + ChatColor.GREEN + "ネザーゲートを開いてもネザー化は有効にならないようになりました。")
                }
                return true
            }
            args[0] == CommandConst.COMMAND_CONFIG_RELOAD -> {
                if (args.size != 1) {
                    sender.sendMessage("" + ChatColor.RED + "引数をつけられないコマンドです。コマンドを確認してください。")
                    sendUsage(sender)
                    return true
                }
                Config.loadConfig(true)
                sender.sendMessage("" + ChatColor.GREEN + "設定をリロードしました。")
                sendConf(sender)
                return true
            }
            args[0] == CommandConst.COMMAND_CONFIG_SET -> {
                if (args.size == 1) {
                    sendConf(sender)
                    return true
                }
                if (args.size != 3) {
                    sender.sendMessage("" + ChatColor.RED + "引数があっていません。コマンドを確認してください。")
                    sendUsage(sender)
                    return true
                }
                when {
                    args[1] == CommandConst.COMMAND_CONFIG_TICK -> {
                        try {
                            Config.tick = args[2].toLong()
                            TaskManager.runConvertTask()
                            sender.sendMessage("" + ChatColor.GREEN + "処理間隔が${Config.tick}Tickに更新されました。")
                        } catch (e: NumberFormatException) {
                            sender.sendMessage("" + ChatColor.RED + "不正な入力です。整数を入力してください。")
                        }
                        return true
                    }
                    args[1] == CommandConst.COMMAND_CONFIG_RANGE -> {
                        try {
                            Config.range = args[2].toInt()
                            sender.sendMessage("" + ChatColor.GREEN + "有効範囲が${Config.range}に更新されました。")
                        } catch (e: NumberFormatException) {
                            sender.sendMessage("" + ChatColor.RED + "不正な入力です。整数を入力してください。")
                        }
                        return true
                    }
                }
                sender.sendMessage("" + ChatColor.RED + "設定が見つかりませんでした。コマンドを確認してください。")
                sendConf(sender)
                return
            }
            args[0] == CommandConst.COMMAND_CONVERT_BLOCK_ADD -> {
                if (args.size != 4) {
                    sender.sendMessage("" + ChatColor.RED + "引数があっていません。コマンドを確認してください。")
                    sendUsage(sender)
                    return true
                }
                var check = Material.getMaterial(args[1].toUpperCase())
                if (check == null){
                    sender.sendMessage("" + ChatColor.RED + "${args[1]}は存在しません。")
                    return true
                }
                if (check != null && !check.isBlock){
                    sender.sendMessage("" + ChatColor.RED + "${args[1]}はブロックではありません。")
                    return true
                }
                check = Material.getMaterial(args[2].toUpperCase())
                if (check == null){
                    sender.sendMessage("" + ChatColor.RED + "${args[2]}は存在しません。")
                    return true
                }
                if (check != null && !check.isBlock){
                    sender.sendMessage("" + ChatColor.RED + "${args[2]}はブロックではありません。")
                    return true
                }
                if (args[3] != CommandConst.COMMAND_CONVERT_BLOCK_OVERWORLD &&
                        args[3] != CommandConst.COMMAND_CONVERT_BLOCK_NETHER ){
                    sender.sendMessage("" + ChatColor.RED + "3番目の引数は${CommandConst.COMMAND_CONVERT_BLOCK_OVERWORLD}または${CommandConst.COMMAND_CONVERT_BLOCK_NETHER}のみ有効です。")
                    return true
                }
                when (args[3]) {
                    CommandConst.COMMAND_CONVERT_BLOCK_OVERWORLD -> {
                        Config.nomalWorldConvertList[args[1].toUpperCase()] = check
                        sender.sendMessage("" + ChatColor.GREEN + "${args[1]} => ${args[2]}(${args[3]})の変換を追加しました。")
                        return true
                    }
                    CommandConst.COMMAND_CONVERT_BLOCK_NETHER -> {
                        Config.netherWorldConvertList[args[1].toUpperCase()] = check
                        sender.sendMessage("" + ChatColor.GREEN + "${args[1]} => ${args[2]}(${args[3]})の変換を追加しました。")
                        return true
                    }
                }
            }
            args[0] == CommandConst.COMMAND_CONVERT_BLOCK_REMOVE -> {
                if (args.size != 3) {
                    sender.sendMessage("" + ChatColor.RED + "引数があっていません。コマンドを確認してください。")
                    return true
                }
                var check = Material.getMaterial(args[1].toUpperCase())
                if (check == null){
                    sender.sendMessage("" + ChatColor.RED + "${args[1]}は存在しません。")
                    return true
                }
                if (args[2] != CommandConst.COMMAND_CONVERT_BLOCK_OVERWORLD &&
                        args[2] != CommandConst.COMMAND_CONVERT_BLOCK_NETHER ){
                    sender.sendMessage("" + ChatColor.RED + "2番目の引数は${CommandConst.COMMAND_CONVERT_BLOCK_OVERWORLD}または${CommandConst.COMMAND_CONVERT_BLOCK_NETHER}のみ有効です。")
                    return true
                }
                when (args[2]) {
                    CommandConst.COMMAND_CONVERT_BLOCK_OVERWORLD -> {
                        val removeName = check.name
                        if (!Config.nomalWorldConvertList.containsKey(removeName)){
                            sender.sendMessage("" + ChatColor.RED + "変更対象がありません。")
                            return true
                        }
                        Config.nomalWorldConvertList.remove(removeName)
                        sender.sendMessage("" + ChatColor.GREEN + "${args[1]} => ${removeName}(${args[2]})の変換を削除しました。")
                        return true
                    }
                    CommandConst.COMMAND_CONVERT_BLOCK_NETHER -> {
                        val removeName = check.name
                        if (!Config.netherWorldConvertList.containsKey(removeName)){
                            sender.sendMessage("" + ChatColor.RED + "変更対象がありません。")
                            return true
                        }
                        Config.netherWorldConvertList.remove(removeName)
                        sender.sendMessage("" + ChatColor.GREEN + "${args[1]} => ${removeName}(${args[2]})の変換を削除しました。")
                        return true
                    }
                }
            }
        }
        sendUsage(sender)
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        var completions: MutableList<String> = mutableListOf()
        when (args.size) {
            1 -> {
                completions.addAll(listOf(CommandConst.COMMAND_ADD,
                        CommandConst.COMMAND_REMOVE,
                        CommandConst.COMMAND_GATE_SWITCH,
                        CommandConst.COMMAND_CONFIG_SET,
                        CommandConst.COMMAND_CONFIG_RELOAD,
                        CommandConst.COMMAND_CONVERT_BLOCK_ADD,
                        CommandConst.COMMAND_CONVERT_BLOCK_REMOVE).filter { e -> e.startsWith(args[0]) })
            }
            2 -> {
                when {
                    args[0] == CommandConst.COMMAND_ADD -> {
                        var completions_tmp: MutableList<String> = mutableListOf()
                        completions_tmp.addAll(listOf("@a", "@p", "@r", "@s"))
                        completions_tmp.addAll(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()))
                        completions.addAll(completions_tmp.stream()
                                .filter{ e -> !GameManager.converterPlayers.contains(Bukkit.getPlayer(e)?.uniqueId) }
                                .filter{ e -> e.startsWith(args[1]) }
                                .collect(Collectors.toList()))
                    }
                    args[0] == CommandConst.COMMAND_REMOVE -> {
                        var completions_tmp: MutableList<String> = mutableListOf()
                        completions_tmp.addAll(listOf("@a", "@p", "@r", "@s"))
                        completions_tmp.addAll(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()))
                        completions.addAll(completions_tmp.stream()
                                .filter{ e -> GameManager.converterPlayers.contains(Bukkit.getPlayer(e)?.uniqueId) || e.startsWith("@") }
                                .filter{ e -> e.startsWith(args[1]) }
                                .collect(Collectors.toList()))
                    }
                    args[0] == CommandConst.COMMAND_CONFIG_SET -> {
                        completions.addAll(listOf(CommandConst.COMMAND_CONFIG_TICK, CommandConst.COMMAND_CONFIG_RANGE).filter{ e -> e.startsWith(args[1]) })
                    }
                    args[0] == CommandConst.COMMAND_CONVERT_BLOCK_ADD -> {
                        var block = mutableListOf<String>()
                        for (mat in Material.values()){
                            if (mat.isBlock) {
                                block.add(mat.name)
                            }
                        }
                        completions.addAll(block.filter{ e -> e.startsWith(args[1]) })
                    }
                    args[0] == CommandConst.COMMAND_CONVERT_BLOCK_REMOVE -> {
                        var block = mutableListOf<String>()
                        for (matName in Config.nomalWorldConvertList.keys){
                            block.add(matName)
                        }
                        for (matName in Config.netherWorldConvertList.keys){
                            block.add(matName)
                        }
                        completions.addAll(block.distinct().filter{ e -> e.startsWith(args[1]) })
                    }
                }
            }
            3 -> {
                when {
                    args[1] == CommandConst.COMMAND_CONFIG_TICK || args[1] == CommandConst.COMMAND_CONFIG_RANGE -> {
                        completions.add("<数字>")
                    }
                    args[0] == CommandConst.COMMAND_CONVERT_BLOCK_ADD -> {
                        var block = mutableListOf<String>()
                        for (mat in Material.values()){
                            if (mat.isBlock) {
                                block.add(mat.name)
                            }
                        }
                        completions.addAll(block.filter{ e -> e.startsWith(args[2]) }.filter { e -> e != args[1] })
                    }
                    args[0] == CommandConst.COMMAND_CONVERT_BLOCK_REMOVE -> {
                        completions.addAll(listOf(CommandConst.COMMAND_CONVERT_BLOCK_OVERWORLD,
                                CommandConst.COMMAND_CONVERT_BLOCK_NETHER
                        ).filter { e -> e.startsWith(args[2]) })
                    }
                }
            }
            4 -> {
                when {
                    args[0] == CommandConst.COMMAND_CONVERT_BLOCK_ADD -> {
                        completions.addAll(listOf(CommandConst.COMMAND_CONVERT_BLOCK_OVERWORLD,
                                CommandConst.COMMAND_CONVERT_BLOCK_NETHER
                        ).filter { e -> e.startsWith(args[3]) })
                    }
                }
            }
        }
        return completions
    }
    private fun sendUsage(sender: CommandSender){
        val usagePrefix = "  ${CommandConst.MAIN_COMMAND} "
        val descPrefix = "    ";
        sender.sendMessage("" + ChatColor.GREEN + "Usage:");
        sender.sendMessage("${usagePrefix}${CommandConst.COMMAND_ADD}")
        sender.sendMessage("${descPrefix}指定されたプレイヤーの周囲をネザー化")
        sender.sendMessage("${usagePrefix}${CommandConst.COMMAND_REMOVE}")
        sender.sendMessage("${descPrefix}指定されたプレイヤーの周囲ネザー化を停止")
        sender.sendMessage("${usagePrefix}${CommandConst.COMMAND_GATE_SWITCH}")
        sender.sendMessage("${descPrefix}ネザーゲートポータル作成プレイヤーの周囲ネザー化on/off切り替え")
        sender.sendMessage("${usagePrefix}${CommandConst.COMMAND_CONFIG_RELOAD}")
        sender.sendMessage("${descPrefix}conf-setで指定できる値と${CommandConst.COMMAND_GATE_SWITCH}で設定した状態の初期化")
        sender.sendMessage("${usagePrefix}${CommandConst.COMMAND_CONFIG_SET} ${CommandConst.COMMAND_CONFIG_TICK} <数字>")
        sender.sendMessage("${descPrefix}ネザー化処理の処理間隔(Tick, デフォルト10)")
        sender.sendMessage("${usagePrefix}${CommandConst.COMMAND_CONFIG_SET} ${CommandConst.COMMAND_CONFIG_RANGE} <数字>")
        sender.sendMessage("${descPrefix}ネザー化処理の範囲(デフォルト6)")
    }
    private fun sendConf(sender: CommandSender){
        sender.sendMessage("" + ChatColor.GREEN + "設定値一覧:")
        sender.sendMessage("  ${CommandConst.COMMAND_CONFIG_TICK}: ${Config.tick}")
        sender.sendMessage("  ${CommandConst.COMMAND_CONFIG_RANGE}: ${Config.range}")
        sender.sendMessage("  ${CommandConst.COMMAND_GATE_SWITCH}: ${if (Config.isGateAppending) "on" else "off"}")
        var showList = mutableListOf<String>()
        for (p in Bukkit.getOnlinePlayers().stream().filter{ e -> GameManager.converterPlayers.contains(e.player?.uniqueId)}.collect(Collectors.toList())){
            showList.add(p.name)
        }
        sender.sendMessage("  ネザー化プレイヤー: $showList")
    }
}
package me.maxos.stalker.remains.command

import me.maxos.stalker.remains.deadbody.management.DeadBodyManager
import me.maxos.stalker.remains.deadbody.management.Reason
import me.maxos.stalker.remains.debug.sendDebug
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Executor(
	private val deadBodyManager: DeadBodyManager
): CommandExecutor {

	private val playerCommands = setOf<String>("spawnnpc", "deletenpc")

	override fun onCommand(
		sender: CommandSender,
		cmd: Command,
		str: String,
		args: Array<out String>?
	): Boolean {
		if (args.isNullOrEmpty()) return false
		args[0].let {
			if (!onlyPlayer(sender, it)) return false // если команда недоступна для консоли - ретурним
			val player = sender as Player // теперь мы точно знаем, что сендер - существующий игрок
			when (it) {
				"spawn" -> {
				//	npcManager.createNpc(player.location, player)
					deadBodyManager.createDeadBody(player, Reason.MANUALLY)
				}
				"clear" -> {
				//	npcManager.clearAllNpc()
					deadBodyManager.clearAllDeadBody()
				}
				"remove" -> {
					val entity = player.getTargetEntity(10) ?: run {
						sendDebug("${player.name} не смотрит на энтити!")
						return true
					}
					val entityId = entity.uniqueId
					sendDebug("UUID: $entityId")
					player.sendMessage("Вы смотрите на энтити ${entity.type}")
					deadBodyManager.removeDeadBody(entityId)
				}
			}
		}
		return false
	}

	// Если сендер не является игроком, а команда является доступной только игрокам
	// То возвращаем false | иначе true
	private fun onlyPlayer(sender: CommandSender, cmd: String): Boolean {
		if (sender !is Player && playerCommands.contains(cmd)) {
			sender.sendMessage("Эту команду может использовать только игрок!")
			return false
		} else return true
	}
}
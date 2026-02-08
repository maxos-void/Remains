package me.maxos.stalker.remains.command

import me.maxos.stalker.remains.database.DataBaseManager
import me.maxos.stalker.remains.deadbody.management.DeadBodyManager
import me.maxos.stalker.remains.deadbody.management.Reason
import me.maxos.stalker.remains.utils.InventoryUtils.getAllItems
import me.maxos.stalker.remains.utils.debug.Debuger.sendDebug
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Executor(
	private val deadBodyManager: DeadBodyManager,
	private val dataBaseManager: DataBaseManager
): CommandExecutor {

	private val playerCommands = setOf("spawnnpc", "deletenpc")

	override fun onCommand(
		sender: CommandSender,
		cmd: Command,
		str: String,
		args: Array<out String>
	): Boolean {
		if (args.isEmpty()) return false
		args[0].let {
			if (!onlyPlayer(sender, it)) return false // если команда недоступна для консоли - ретурним
			val player = sender as Player // теперь мы точно знаем, что сендер - существующий игрок
			when (it) {
				"spawn" -> {
					//	npcManager.createNpc(player.location, player)
					deadBodyManager.createDeadBody(player, Reason.MANUALLY, false, player.inventory.getAllItems())
					return true
				}
				"clear" -> {
					//	npcManager.clearAllNpc()
					deadBodyManager.clearAllDeadBody()
					return true
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
					return true
				}

				"debug" -> {
					deadBodyManager.debug()
					return true
				}
				"debugdb" -> {
					dataBaseManager.debug()
					return true
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
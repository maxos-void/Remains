package me.maxos.stalker.remains.events

import me.maxos.stalker.remains.deadbody.management.DeadBodyManager
import me.maxos.stalker.remains.utils.debug.Debuger.sendDebug
import me.maxos.stalker.remains.utils.debug.Debuger.sendErrorDebug
import org.bukkit.entity.ArmorStand
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent

class ClickOnRemainsEvent(
	private val deadBodyManager: DeadBodyManager
): Listener {

	@EventHandler
	fun onClick(e: PlayerInteractAtEntityEvent) {

		val player = e.player
		val entity = e.rightClicked as? ArmorStand ?: return
		val entityId = entity.uniqueId
		sendDebug("${player.name} кликнул по арморстенду! (ещё не проверили, наш ли это стенд)")

		if (!deadBodyManager.isBodyStand(entityId)) return
		e.isCancelled = true

		val standId = entityId // теперь мы знаем,
		// что это наш стенд точно, поэтому обращаемся к нему не как к обычному энтити
		val inventory = deadBodyManager.getDeadBodyInventory(standId) ?: run {
			sendErrorDebug("У трупа с standId $standId не был обнаружен ивнентарь!")
			return
		}
		player.openInventory(inventory)
		sendDebug("Открываем игроку ${player.name} инвентарь трупа!")
	}

}
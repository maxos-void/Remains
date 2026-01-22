package me.maxos.stalker.remains.events

import me.maxos.stalker.remains.deadbody.management.DeadBodyManager
import me.maxos.stalker.remains.debug.sendDebug
import me.maxos.stalker.remains.debug.sendErrorDebug
import org.bukkit.entity.ArmorStand
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent

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
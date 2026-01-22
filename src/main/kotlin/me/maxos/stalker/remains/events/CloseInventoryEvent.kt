package me.maxos.stalker.remains.events

import me.maxos.stalker.remains.deadbody.inventory.DeadBodyInventory
import me.maxos.stalker.remains.deadbody.management.DeadBodyManager
import me.maxos.stalker.remains.debug.sendDebug
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent

class CloseInventoryEvent(
	private val deadBodyManager: DeadBodyManager
): Listener {

	@EventHandler
	fun onCloseDeadBodyInventory(e: InventoryCloseEvent) {
		val inventory = e.inventory
		val holder = inventory.holder
		if (holder !is DeadBodyInventory) return

		val viewersAmount = inventory.viewers.size
		val isEmpty = inventory.contents.none { itemStack -> itemStack != null }
		if (viewersAmount <= 1 && isEmpty) {
			val standId = holder.getDeadBody().bodyStand.standId
			deadBodyManager.removeDeadBody(standId)
		}
	}

}
package me.maxos.stalker.remains.events

import me.maxos.stalker.remains.deadbody.management.DeadBodyManager
import me.maxos.stalker.remains.deadbody.management.Reason
import me.maxos.stalker.remains.utils.InventoryUtils.getAllItems
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class QuitEvent(
	private val manager: DeadBodyManager
): Listener {

	@EventHandler
	fun onQuit(e: PlayerQuitEvent) {
		val player = e.player
		val inv = player.inventory
		val drops = inv.getAllItems()

		if (drops.isNotEmpty()) {
			inv.clear()
			manager.createDeadBody(player, Reason.DEATH, true, drops)
		}

	}

}
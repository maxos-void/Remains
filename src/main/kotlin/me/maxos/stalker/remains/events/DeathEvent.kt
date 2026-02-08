package me.maxos.stalker.remains.events

import me.maxos.stalker.remains.deadbody.management.DeadBodyManager
import me.maxos.stalker.remains.deadbody.management.Reason
import me.maxos.stalker.remains.utils.debug.Debuger.sendDebug
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class DeathEvent(
	private val manager: DeadBodyManager
): Listener {

	@EventHandler
	fun onPlayerDeath(e: PlayerDeathEvent) {
		val player = e.entity
		val drops = e.drops.map { itemStack -> itemStack.clone() }.toList()

		if (drops.isNotEmpty()) {
			e.drops.clear()
			manager.createDeadBody(player, Reason.DEATH, false, drops)
			sendDebug("Игрок ${player.name} погиб! Создаём его труп")
		}
	}
}
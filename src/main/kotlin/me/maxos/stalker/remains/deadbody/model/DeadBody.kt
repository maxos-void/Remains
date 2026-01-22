package me.maxos.stalker.remains.deadbody.model

import me.maxos.stalker.remains.deadbody.inventory.DeadBodyInventory
import me.maxos.stalker.remains.debug.sendDebug
import org.bukkit.inventory.ItemStack
import java.util.UUID

data class DeadBody(
	val bodyStand: BodyStand,
	val playerId: UUID,
	val playerName: String,
	val items: List<ItemStack>
) {
	val inventory = DeadBodyInventory(
		"Труп $playerName",
		27,
		items,
		this
	).inventory

	fun killStand() {
		bodyStand.kill()
		sendDebug("Стенд трупа игрока $playerId был удалён физически!")
	}
}
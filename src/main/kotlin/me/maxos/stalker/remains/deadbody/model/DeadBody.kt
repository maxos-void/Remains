package me.maxos.stalker.remains.deadbody.model

import me.maxos.stalker.remains.deadbody.inventory.DeadBodyInventory
import me.maxos.stalker.remains.file.config.model.InventoryConfig
import me.maxos.stalker.remains.utils.debug.Debuger.sendDebug
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.UUID

data class DeadBody(
	val bodyStand: BodyStand,
	val playerId: UUID,
	val playerName: String,
	val items: List<ItemStack>? = null,
	val unloadInventory: Inventory? = null,
	val isInventoryRandom: Boolean,
	val isSleeper: Boolean = false,
	private val config: InventoryConfig
) {
	val inventory = if (items != null) {
		DeadBodyInventory(
			config,
			size = 27,
			items = items,
			deadBody = this,
			isRandom = isInventoryRandom,
			isSleeper = isSleeper,
		).inventory
	} else {
		DeadBodyInventory(
			config,
			size = 27,
			unloadInventory = unloadInventory,
			deadBody = this,
			isRandom = isInventoryRandom,
			isSleeper = isSleeper
		).inventory
	}

	fun killStand() {
		bodyStand.kill()
		sendDebug("Стенд трупа игрока $playerId был удалён физически!")
	}
}
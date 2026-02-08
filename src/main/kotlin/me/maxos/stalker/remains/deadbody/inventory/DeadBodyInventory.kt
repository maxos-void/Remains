package me.maxos.stalker.remains.deadbody.inventory

import me.maxos.stalker.remains.deadbody.model.DeadBody
import me.maxos.stalker.remains.file.config.model.InventoryConfig
import me.maxos.stalker.remains.utils.debug.Debuger.sendDebug
import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

class DeadBodyInventory(
	private val config: InventoryConfig,
	private val size: Int = config.size,
	private val items: List<ItemStack>? = null,
	private val unloadInventory: Inventory? = null,
	private val deadBody: DeadBody,
	private val isRandom: Boolean,
	private val isSleeper: Boolean
) : InventoryHolder {
	private val title = (if (isSleeper) config.titleSleeper else config.titleRemain)
		.replace("{player-name}", deadBody.playerName)
	private val inventory: Inventory = Bukkit.createInventory(
		this,
		size,
		title
	).also {
		unloadInventory?.contents?.forEachIndexed { index, item ->
			it.setItem(index, item)
		}
	}

	fun getDeadBody() = deadBody

	init {
		if (!items.isNullOrEmpty()) {
			if (isRandom) randomFilling()
			else defaultFilling()
		}
		sendDebug("Создаём инвентарь трупа")
		sendDebug("Предметов: ${items?.size}")
		sendDebug("Тайтл: $title")
	}

	override fun getInventory(): Inventory {
		return inventory
	}

	private fun defaultFilling() {
		items?.forEach { item ->
			val result = inventory.addItem(item)
			if (result.isNotEmpty()) {
				val location = deadBody.bodyStand.stand.location.clone()
				val world = location.world
				result.values.forEach { item ->
					world.dropItem(location, item.clone())
				}
			}
		}
	}

	private fun randomFilling() {

		val slotsRange = (0 until size)
		val useSlots = hashSetOf<Int>()
		val iterator = items?.iterator() ?: return

		slotsRange.forEach { _ ->
			if (iterator.hasNext()) {
				val item = iterator.next()
				var randomSlot = slotsRange.random()
				while (randomSlot in useSlots) randomSlot = slotsRange.random()
				useSlots.add(randomSlot)
				inventory.setItem(randomSlot, item)
			} else return
		}
	}
}
package me.maxos.stalker.remains.deadbody.inventory

import me.maxos.stalker.remains.deadbody.model.DeadBody
import me.maxos.stalker.remains.debug.sendDebug
import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

class DeadBodyInventory(
	private val title: String = "Труп",
	private val size: Int = 45,
	private val items: List<ItemStack>,
	private val deadBody: DeadBody,
	private val isRandom: Boolean
) : InventoryHolder {
	private val inventory: Inventory = Bukkit.createInventory(
		this,
		size,
		title
	)

	fun getDeadBody() = deadBody

	init {
		if (items.isNotEmpty()) {
			if (isRandom) randomFilling()
		}
		sendDebug("Создаём инвентарь трупа")
		sendDebug("Предметов: ${items.size}")
		sendDebug("Тайтл: $title")
	}

	override fun getInventory(): Inventory {
		return inventory
	}

	private fun defaultFilling() {
		inventory.addItem()
	}

	private fun randomFilling() {

		val slotsRange = (0..<size)
		val useSlots = hashSetOf<Int>()
		val iterator = items.iterator()

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
package me.maxos.stalker.remains.utils

import org.bukkit.Material
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

object InventoryUtils {
	fun PlayerInventory.getAllItems(): List<ItemStack> {
		val items = mutableListOf<ItemStack>()
		this.contents.forEach { itemStack -> if (itemStack != null) items.add(itemStack.clone()) }
		return items
	}
}
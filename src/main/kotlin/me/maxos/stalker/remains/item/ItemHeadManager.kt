package me.maxos.stalker.remains.item

import me.maxos.stalker.remains.file.config.model.RemainsItemsConfig
import org.bukkit.inventory.ItemStack

class ItemHeadManager(
	private val remainsItemsConfig: RemainsItemsConfig
) {

	fun getItem(): ItemStack {
		return if (remainsItemsConfig.random) itemHeads.random().clone()
		else itemHeads.first().clone()
	}

	private val itemHeads = hashSetOf<ItemStack>()

	init {
		createItemStacks()
	}

	private fun createItemStacks() {
		itemHeads.addAll(
			remainsItemsConfig.items.map { itemModel ->
				ItemStack(itemModel.material).apply {
					val itemMeta = this.itemMeta
					itemMeta.setCustomModelData(itemModel.customModelData)
					this.itemMeta = itemMeta
				}
			}
		)
	}
}
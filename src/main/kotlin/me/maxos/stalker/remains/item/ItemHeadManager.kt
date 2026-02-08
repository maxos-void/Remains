package me.maxos.stalker.remains.item

import me.maxos.stalker.remains.file.config.model.RemainsItemsConfig
import me.maxos.stalker.remains.utils.debug.Debuger.sendDebug
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ItemHeadManager(
	private val remainsItemsConfig: RemainsItemsConfig?
) {
	private val default = ItemStack(Material.PLAYER_HEAD)
	private val itemHeads = hashSetOf<ItemStack>()

	init {
		createItemStacks()
	}

	private fun createItemStacks() {
		remainsItemsConfig ?: return
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

	fun getItem(): ItemStack {
		remainsItemsConfig ?: run {
			sendDebug("Конфиг предметов не обнаружен, выбираем стандартный ItemStack")
			return default
		}
		return if (remainsItemsConfig.random) {
			sendDebug("Рандом включен, выбираем ItemStack")
			itemHeads.random().clone()
		}
		else {
			sendDebug("Рандом выключен, берём первый из списка ItemStack")
			itemHeads.first().clone()
		}
	}

}
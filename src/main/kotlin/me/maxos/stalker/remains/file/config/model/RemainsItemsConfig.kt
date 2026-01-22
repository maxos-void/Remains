package me.maxos.stalker.remains.file.config

import me.maxos.stalker.remains.deadbody.model.item.ItemModel

data class RemainsItemsConfig(
	val random: Boolean,
	val items: List<ItemModel>
)

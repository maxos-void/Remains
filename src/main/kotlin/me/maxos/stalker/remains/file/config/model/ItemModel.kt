package me.maxos.stalker.remains.file.config.model

import org.bukkit.Material

data class ItemModel(
	val modelName: String,
	val material: Material,
	val customModelData: Int
)
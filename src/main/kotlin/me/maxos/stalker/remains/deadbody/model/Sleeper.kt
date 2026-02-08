package me.maxos.stalker.remains.deadbody.model

import me.maxos.stalker.remains.deadbody.management.DeadBodyManager
import me.maxos.stalker.remains.utils.database.Base64Utils.fromInventoryBase64
import me.maxos.stalker.remains.utils.database.Base64Utils.fromItemBase64
import me.maxos.stalker.remains.utils.database.LocationSerializer.fromLocationText
import org.bukkit.Location
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.UUID

data class Sleeper(
	val standId: UUID,
	val playerName: String,
	val standHeadItemHash: String, //ItemStack,
	val locationHash: String,// Location,
	val inventoryHash: String// Inventory
) {
	val standHeadItem: ItemStack by lazy { fromItemBase64(standHeadItemHash) }
	val location: Location? by lazy { fromLocationText(locationHash) }
	val inventory: Inventory by lazy { fromInventoryBase64(inventoryHash) }
}

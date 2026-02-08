package me.maxos.stalker.remains.deadbody.model

import org.bukkit.Bukkit
import org.bukkit.entity.ArmorStand
import org.bukkit.inventory.ItemStack
import java.util.UUID

data class BodyStand(
	val stand: ArmorStand,
	val standId: UUID,
	val itemHead: ItemStack
) {
	val location = stand.location.clone()
	fun kill() {
	//	stand.remove()
		Bukkit.getEntity(standId)?.remove()
	}
}

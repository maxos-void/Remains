package me.maxos.stalker.remains.deadbody.model

import org.bukkit.entity.ArmorStand
import java.util.UUID

data class BodyStand(
	val stand: ArmorStand,
	val standId: UUID
) {
	fun kill() {
		stand.remove()
	}
}

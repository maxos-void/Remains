package me.maxos.stalker.remains.deadbody.model

import me.maxos.stalker.remains.debug.sendDebug
import org.bukkit.Bukkit
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

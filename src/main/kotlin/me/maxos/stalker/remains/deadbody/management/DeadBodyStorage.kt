package me.maxos.stalker.remains.deadbody

import me.maxos.stalker.remains.deadbody.model.DeadBody
import me.maxos.stalker.remains.debug.sendDebug
import java.util.UUID

class DeadBodyStorage {

	private val deadBodyStorage = hashMapOf<UUID, DeadBody>()

	fun saveDeadBody(standId: UUID, deadBody: DeadBody) {
		deadBodyStorage[standId] = deadBody
		sendDebug("Сохраняем для стенда $standId тело $deadBody")
	}
}
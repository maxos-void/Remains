package me.maxos.stalker.remains.deadbody.management

import me.maxos.stalker.remains.deadbody.model.DeadBody
import me.maxos.stalker.remains.debug.sendDebug
import java.util.UUID

class DeadBodyStorage {

	private val deadBodyStorage = hashMapOf<UUID, DeadBody>()

	fun saveDeadBody(standId: UUID, deadBody: DeadBody) {
		deadBodyStorage[standId] = deadBody
		sendDebug("Сохраняем для стенда $standId тело $deadBody")
	}

	fun searchDeadBody(standId: UUID) = deadBodyStorage[standId]
	fun getAllDeadBody() = deadBodyStorage.values

	fun remove(standId: UUID) { deadBodyStorage.remove(standId) }
	fun clear() { deadBodyStorage.clear() }

	fun getName(standId: UUID) = deadBodyStorage[standId]?.playerName
	fun getPlayerId(standId: UUID) = deadBodyStorage[standId]?.playerId
	fun getInventory(standId: UUID) = deadBodyStorage[standId]?.inventory
}
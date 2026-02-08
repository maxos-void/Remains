package me.maxos.stalker.remains.deadbody.management

import me.maxos.stalker.remains.deadbody.model.DeadBody
import me.maxos.stalker.remains.utils.debug.Debuger.sendDebug
import java.util.UUID

class DeadBodyStorage {

	private val deadBodyStorage = hashMapOf<UUID, DeadBody>()

	fun saveDeadBody(standId: UUID, deadBody: DeadBody) {
		deadBodyStorage[standId] = deadBody
		sendDebug("Сохраняем для стенда $standId тело $deadBody")
		if (deadBody.isSleeper) {
			sendDebug("$standId сохранён как слиппер")
		}
	}

	fun searchDeadBody(standId: UUID) = deadBodyStorage[standId]
	fun getAllDeadBody() = deadBodyStorage.values
	fun getMap() = deadBodyStorage.mapValues { (_, deadBody) -> deadBody.bodyStand.location }
	fun getAllSleepers() = deadBodyStorage.values.filter { it.isSleeper }

	fun remove(standId: UUID) { deadBodyStorage.remove(standId) }
	fun clear() { deadBodyStorage.clear() }

	fun getName(standId: UUID) = deadBodyStorage[standId]?.playerName
	fun getPlayerId(standId: UUID) = deadBodyStorage[standId]?.playerId
	fun getInventory(standId: UUID) = deadBodyStorage[standId]?.inventory
}
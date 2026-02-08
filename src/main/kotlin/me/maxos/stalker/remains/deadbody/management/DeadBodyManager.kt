package me.maxos.stalker.remains.deadbody.management


import me.maxos.stalker.remains.deadbody.model.Sleeper
import me.maxos.stalker.remains.file.UuidData
import me.maxos.stalker.remains.utils.debug.Debuger.sendDebug
import me.maxos.stalker.remains.utils.logError
import me.maxos.stalker.remains.utils.logInfo
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.UUID


class DeadBodyManager(
	private val storage: DeadBodyStorage,
	private val standCreator: StandCreator,
	private val deadBodyTimer: DeadBodyTimer,
	private val uuidData: UuidData
) {

	fun debug() {
		sendDebug("\n\nКОЛИЧЕСТВО ТРУПОВ: ${storage.getAllDeadBody().size}\n\n")
		sendDebug("${storage.getAllDeadBody()}\n\n")
	}

	fun clear() {
		storage.getAllDeadBody().filter { !it.isSleeper }.forEach { deadBody ->
			deadBody.bodyStand.location.clone().chunk.load()
			Bukkit.getEntity(deadBody.bodyStand.standId)?.remove()
		}
	}

	fun getStorage() = storage

	fun uploadedSleeper(sleepers: List<Sleeper>) {
		sleepers.forEach {
			standCreator.createIsDb(it)
			sendDebug("Слиппер $it загрузился успешно!")
		}
		logInfo("Все слипперы выгружены из базы данных!")

		cleaningOutdatedEntity()
	}

	private fun cleaningOutdatedEntity() {
		val savedDate = uuidData.loadUuids()
		val actualityDate = storage.getAllDeadBody().map { it.bodyStand.standId }.toSet()
		savedDate.filter { (id, _) -> id !in actualityDate }.forEach { (id, loc) ->
			loc.chunk.load()
			Bukkit.getEntity(id)?.let {
				it.remove()
				sendDebug("Энтити $id неактуален! Удаляем его из мира")
			}
		}

	}

	fun saveDataUuid() {
		uuidData.saveUuids(
			storage.getMap()
		)
	}

	fun createDeadBody(player: Player, reason: Reason, isSleeper: Boolean, drops: List<ItemStack>) {

		val deadBody = standCreator.createIsPlayer(player, drops, isSleeper) ?: run {
			logError("Не удалось создать труп игрока ${player.name}")
			return
		}

		when(reason) {
			Reason.DEATH -> {
				sendDebug("Труп был создан, так как игрок ${player.name} погиб!")
			}
			Reason.MANUALLY -> {
				sendDebug("Труп был создан вручную")
			}
		}

		deadBodyTimer.timerDeletion(deadBody, isSleeper,::removeDeadBody)

	}

	fun isBodyStand(entityId: UUID): Boolean {
		return storage.searchDeadBody(entityId) != null
	}

	fun getDeadBodyInventory(standId: UUID) = storage.getInventory(standId)

	fun removeDeadBody(entityId: UUID) {
		val deadBody = storage.searchDeadBody(entityId) ?: run {
			sendDebug("Труп с uuid $entityId не был обнаружен в хранилище! Удаление прошло неудачно!")
			return
		}
		deadBody.killStand()
		storage.remove(deadBody.bodyStand.standId)
		deadBodyTimer.stopTimer(entityId)
	}

	fun clearAllDeadBody() {
		storage.apply {
			getAllDeadBody().forEach { deadBody ->
				deadBody.killStand()
			}
			clear()
		}
		deadBodyTimer.stopAllTimer()
	}

}
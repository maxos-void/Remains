package me.maxos.stalker.remains.deadbody.management


import me.maxos.stalker.remains.debug.sendDebug
import org.bukkit.entity.Player
import java.util.UUID


class DeadBodyManager(
	private val storage: DeadBodyStorage,
	private val standCreator: StandCreator
) {

	fun getStorage() = storage
	fun createDeadBody(player: Player, reason: Reason) {
		standCreator.create(player)
		when(reason) {
			Reason.DEATH -> {
				sendDebug("Труп был создан вручную")
			}
			Reason.MANUALLY -> {
				sendDebug("Труп был создан, так как игрок ${player.name} погиб!")
			}
		}
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
	}

	fun clearAllDeadBody() {
		storage.apply {
			getAllDeadBody().forEach { deadBody ->
				deadBody.killStand()
			}
			clear()
		}
	}

}
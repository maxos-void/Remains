package me.maxos.stalker.remains.deadbody


import org.bukkit.entity.Player


class DeadBodyManager(
	private val storage: DeadBodyStorage,
	private val deadBodyCreator: DeadBodyCreator
) {

	fun getStorage() = storage

	fun playerDeath(player: Player) {
		// создаем труп
		deadBodyCreator.create(player)
	}

}
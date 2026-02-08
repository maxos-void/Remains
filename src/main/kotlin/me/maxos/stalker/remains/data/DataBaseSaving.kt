package me.maxos.stalker.remains.data

import me.maxos.stalker.remains.database.DataBaseManager
import me.maxos.stalker.remains.deadbody.management.DeadBodyManager
import me.maxos.stalker.remains.deadbody.management.DeadBodyStorage
import me.maxos.stalker.remains.deadbody.model.Sleeper
import me.maxos.stalker.remains.utils.Scheduler
import me.maxos.stalker.remains.utils.database.Base64Utils
import me.maxos.stalker.remains.utils.database.LocationSerializer
import me.maxos.stalker.remains.utils.debug.Debuger
import me.maxos.stalker.remains.utils.debug.Debuger.sendDebug
import kotlin.properties.Delegates
import kotlin.system.measureTimeMillis

class DataBaseSaving(
	private val dbManager: DataBaseManager,
	private val manager: DeadBodyManager,
	private val _duration: Int,
	private val storage: DeadBodyStorage = manager.getStorage()
): Saving {

	override val duration: Int = _duration
	override var taskId: Int by Delegates.notNull()

	init {
		periodicSaving()
	}

	override fun saveData() {
		dbManager.saveSleepers(getSleepers())
	}

	override fun notification() {
		sendDebug("Было вызвано сохранение базы данных")
	}

	private fun getSleepers(): List<Sleeper> {
		return storage.getAllSleepers().map {
			val stand = it.bodyStand
			Sleeper(
				it.bodyStand.standId,
				it.playerName,
				Base64Utils.toItemBase64(stand.itemHead),
				LocationSerializer.toSerializeLocation(stand.location),
				Base64Utils.toInventoryBase64(it.inventory)
			)
		}
	}

	/*
	private var taskId by Delegates.notNull<Int>()

	init {
		periodicSaving()
	}

	private fun periodicSaving() {
		taskId = Scheduler.runAsyncTaskTimer(frequency) {
			Debuger.sendDebug("Начинаем асинхронное сохранение слипперов!")
			push(getSleepers())
		}
	}

	fun forcedSaving() {
		stopAsync()
		Debuger.sendDebug("Начинаем принудительное сохранение слипперов!")
		push(getSleepers())
	}

	private fun stopAsync() {
		Scheduler.stopTask(taskId)
		taskId = -1
	}

	private fun push(sleepers: List<Sleeper>): Long {
		return measureTimeMillis {
			dbManager.saveSleepers(sleepers)
		}.also {
			timeNotification(it)
		}
	}

	private fun timeNotification(time: Long) {
		Debuger.sendDebug("Слипперы сохранились за $time мс")
	}

	private fun getSleepers(): List<Sleeper> {
		return storage.getAllSleepers().map {
			val stand = it.bodyStand
			Sleeper(
				it.bodyStand.standId,
				it.playerName,
				Base64Utils.toItemBase64(stand.itemHead),
				LocationSerializer.toSerializeLocation(stand.location),
				Base64Utils.toInventoryBase64(it.inventory)
			)
		}
	}
	 */
}
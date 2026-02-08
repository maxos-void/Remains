package me.maxos.stalker.remains.data

import me.maxos.stalker.remains.deadbody.management.DeadBodyManager
import me.maxos.stalker.remains.utils.Scheduler
import me.maxos.stalker.remains.utils.debug.Debuger
import me.maxos.stalker.remains.utils.debug.Debuger.sendDebug
import kotlin.properties.Delegates
import kotlin.system.measureTimeMillis

class DataSaving(
	private val manager: DeadBodyManager,
	private val _duration: Int
): Saving {

	override val duration: Int = _duration
	override var taskId: Int by Delegates.notNull()

	init {
		periodicSaving()
	}

	override fun saveData() {
		manager.saveDataUuid()
	}

	override fun notification() {
		sendDebug("Было вызвано сохранение UUID трупов в uuids.json")
	}

}
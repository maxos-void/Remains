package me.maxos.stalker.remains.data

import me.maxos.stalker.remains.utils.Scheduler
import me.maxos.stalker.remains.utils.debug.Debuger
import kotlin.system.measureTimeMillis

interface Saving {

	var taskId: Int
	val duration: Int

	fun saveData()
	fun notification()

	fun periodicSaving() {
		taskId = Scheduler.runAsyncTaskTimer(duration) {
			timer(Type.ASYNC) { saveData() }
		}
	}

	fun forceSaving() {
		stopAsync()
		timer(Type.SYNC) { saveData() }
	}

	private fun stopAsync() {
		Scheduler.stopTask(taskId)
		taskId = -1
	}

	private fun timer(type: Type, unit: () -> Unit) {
		val time = measureTimeMillis {
			unit()
		}
		Debuger.sendDebug("Вызвано: ${type.operation}")
		notification()
		Debuger.sendDebug("Сохранение заняло $time мс")
	}
}
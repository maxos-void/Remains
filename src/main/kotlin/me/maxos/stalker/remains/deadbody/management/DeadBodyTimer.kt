package me.maxos.stalker.remains.deadbody.management

import me.maxos.stalker.remains.deadbody.model.DeadBody
import me.maxos.stalker.remains.file.config.model.CleaningConfig
import me.maxos.stalker.remains.utils.Scheduler.runSyncTaskTimer
import me.maxos.stalker.remains.utils.Scheduler.stopTask
import org.bukkit.Bukkit
import java.util.UUID

class DeadBodyTimer(
	private val cleaningConfig: CleaningConfig?
) {

	private val tasks = hashMapOf<UUID, Int>()

	fun timerDeletion(deadBody: DeadBody, isSleeper: Boolean, deleteOperation: (standId: UUID) -> Unit) {

		val id = deadBody.bodyStand.standId

		val isTimer = if (isSleeper)
			cleaningConfig?.sleeperIsTimer ?: return
		else cleaningConfig?.remainsIsTimer ?: return

		if (isTimer) {
			val time = if (isSleeper) cleaningConfig.sleeperTimer else cleaningConfig.remainsTimer
			val task = runSyncTaskTimer(time) { deleteOperation(id) }
			tasks[id] = task
		}
	}

	fun stopTimer(standId: UUID) {
		stopTask(tasks[standId] ?: return)
		tasks.remove(standId)
	}

	fun stopAllTimer() {
		tasks.values.forEach { task ->
			stopTask(task)
		}
		tasks.clear()
	}

}
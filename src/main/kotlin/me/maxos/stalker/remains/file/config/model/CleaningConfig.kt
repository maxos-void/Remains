package me.maxos.stalker.remains.file.config.model

data class CleaningConfig(
	val remainsIsTimer: Boolean,
	val sleeperIsTimer: Boolean,

	val remainsTimer: Int,
	val sleeperTimer: Int
)
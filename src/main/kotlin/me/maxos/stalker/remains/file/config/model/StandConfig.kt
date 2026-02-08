package me.maxos.stalker.remains.file.config.model

data class StandConfig(
	val gravity: Boolean,
	val x: Double,
	val y: Double,
	val z: Double
) {
	val isChangedPosition: Boolean by lazy { !(x == 0.0 || y == 0.0 || z == 0.0) }
}
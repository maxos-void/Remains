package me.maxos.stalker.remains.utils.debug

import java.util.logging.Logger

object Debuger {
	
	var debugStatus: Boolean = false

	private val debug = Logger.getLogger("Remains Debug")

	fun sendDebug(msg: String) { if (debugStatus) debug.info(msg) }
	fun sendErrorDebug(msgErr: String) { if (debugStatus) debug.severe(msgErr) }

	fun debugUnit(unit: Unit) {
		run { unit }
	}
}

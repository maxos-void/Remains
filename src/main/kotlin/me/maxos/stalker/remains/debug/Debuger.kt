package me.maxos.stalker.remains.debug

import java.util.logging.Logger

var debugStatus: Boolean = true

private val debug = Logger.getLogger("Remains Debug")

fun sendDebug(msg: String) { if (debugStatus) debug.info(msg) }
fun sendErrorDebug(msgErr: String) { if (debugStatus) debug.severe(msgErr) }
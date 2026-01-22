package me.maxos.stalker.remains.utils

import java.util.logging.Logger

private val log = Logger.getLogger("Remains")

fun logInfo(msg: String) { log.info(msg) }
fun logError(msgErr: String) { log.severe(msgErr) }
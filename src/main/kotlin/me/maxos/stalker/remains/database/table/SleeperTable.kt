package me.maxos.stalker.remains.database.table

import org.jetbrains.exposed.sql.Table

const val SLEEPER_TABLE_NAME = "sleepers"

object SleeperTable: Table(SLEEPER_TABLE_NAME) {
	val standId = uuid("stand_uuid").primaryKey()
	val playerName = varchar("player_name", 16)
	val standHeadItem = text("head_item")
	val location = text("location")
	val inventory = text("inventory")
}
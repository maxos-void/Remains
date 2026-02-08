package me.maxos.stalker.remains.database

import me.maxos.stalker.remains.Remains
import me.maxos.stalker.remains.database.table.SleeperTable
import me.maxos.stalker.remains.database.table.SleeperTable.standId
import me.maxos.stalker.remains.deadbody.management.DeadBodyManager
import me.maxos.stalker.remains.deadbody.management.DeadBodyStorage
import me.maxos.stalker.remains.deadbody.model.Sleeper
import me.maxos.stalker.remains.utils.Scheduler.runAsyncTask
import me.maxos.stalker.remains.utils.Scheduler.runSyncTask
import me.maxos.stalker.remains.utils.database.Base64Utils.toInventoryBase64
import me.maxos.stalker.remains.utils.database.Base64Utils.toItemBase64
import me.maxos.stalker.remains.utils.database.LocationSerializer.toSerializeLocation
import me.maxos.stalker.remains.utils.debug.Debuger.sendDebug
import me.maxos.stalker.remains.utils.logInfo
import org.bukkit.Bukkit
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.notInList
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.io.File
import kotlin.system.measureTimeMillis

class DataBaseManager(
	private val plugin: Remains,
	private val manager: DeadBodyManager
) {
	init {
		connect()
		loadSleepers()
	}

	private fun connect() {
		val databaseFile = File(plugin.dataFolder, "database")
		if (!databaseFile.exists()) {
			databaseFile.mkdirs()
		}

		val dbUrl = "jdbc:h2:file:${databaseFile.absolutePath}/remains_database;DB_CLOSE_DELAY=-1"
		Database.connect(
			url = dbUrl,
			driver = "org.h2.Driver"
		)
		// таблицы
		transaction {
			SchemaUtils.create(SleeperTable)
		}
		logInfo("§aБаза данных подключена успешно!")
	}

	fun debug() {
		transaction {
			val sleepers = SleeperTable.selectAll().map { row ->
				Sleeper(
					standId = row[SleeperTable.standId],
					playerName = row[SleeperTable.playerName],
					standHeadItemHash = row[SleeperTable.standHeadItem],
					locationHash = row[SleeperTable.location],
					inventoryHash = row[SleeperTable.inventory]
				)
			}
			sendDebug("\nКОЛИЧЕСТВО СЛИППЕРОВ: ${sleepers.size}\n")
			sendDebug("$sleepers\n")
		}
	}

	fun saveSleepers(sleepers: List<Sleeper>) {
		transaction {

			clearTable(sleepers)

			sleepers.forEach { sleeper ->
				val standId = sleeper.standId
				val sleeperExists = SleeperTable.select {
					SleeperTable.standId eq standId
				}.any()

				if (sleeperExists) {
					SleeperTable.update(
						{
							SleeperTable.standId eq standId
						}
					) { raw ->
						raw[playerName] = sleeper.playerName
						raw[standHeadItem] = sleeper.standHeadItemHash
						raw[location] = sleeper.locationHash
						raw[inventory] = sleeper.inventoryHash
					}
				} else {
					SleeperTable.insert { raw ->
						raw[playerName] = sleeper.playerName
						raw[this.standId] = standId
						raw[standHeadItem] = sleeper.standHeadItemHash
						raw[location] = sleeper.locationHash
						raw[inventory] = sleeper.inventoryHash
					}
				}
			}
		}
	}

	private fun clearTable(sleepers: List<Sleeper>) {
		val passedStandIds = sleepers.map { it.standId }
		SleeperTable.deleteWhere {
			standId notInList passedStandIds
		}
	}

	fun loadSleepers() {

		runAsyncTask {

			var sleepers: List<Sleeper>

			transaction {
				val loadTime = measureTimeMillis {
					sleepers = SleeperTable.selectAll().map { row ->
						Sleeper(
							standId = row[SleeperTable.standId],
							playerName = row[SleeperTable.playerName],
							standHeadItemHash = row[SleeperTable.standHeadItem],
							locationHash = row[SleeperTable.location],
							inventoryHash = row[SleeperTable.inventory]
						)
					}
					if (sleepers.isEmpty()) {
						sendDebug("Слипперы в базе не обнаружены, загружать нечего")
						return@transaction
					}
				}

				val initTime = measureTimeMillis { initSleepers(sleepers) }

				sendDebug("Загрузка слипперов заняла $loadTime мс (асинхрон)")
				sendDebug("Инициализация (создание) слипперов заняла $initTime мс (синхрон)")
			}
		}
	}

	private fun initSleepers(sleepers: List<Sleeper>) {
		runSyncTask {
			manager.uploadedSleeper(sleepers)
		}
	}
}
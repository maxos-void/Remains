package me.maxos.stalker.remains

import me.maxos.stalker.remains.Remains.CommandName.MAIN_COMMAND
import me.maxos.stalker.remains.Remains.FileName.SETTINGS_FILE_NAME
import me.maxos.stalker.remains.command.Executor
import me.maxos.stalker.remains.command.TabCompleter
import me.maxos.stalker.remains.database.DataBaseManager
import me.maxos.stalker.remains.data.DataBaseSaving
import me.maxos.stalker.remains.data.DataSaving
import me.maxos.stalker.remains.deadbody.management.StandCreator
import me.maxos.stalker.remains.deadbody.management.DeadBodyManager
import me.maxos.stalker.remains.deadbody.management.DeadBodyStorage
import me.maxos.stalker.remains.deadbody.management.DeadBodyTimer
import me.maxos.stalker.remains.events.ClickOnRemainsEvent
import me.maxos.stalker.remains.events.CloseInventoryEvent
import me.maxos.stalker.remains.events.DeathEvent
import me.maxos.stalker.remains.events.QuitEvent
import me.maxos.stalker.remains.file.FileManager
import me.maxos.stalker.remains.file.UuidData
import me.maxos.stalker.remains.file.config.ConfigManager
import me.maxos.stalker.remains.item.ItemHeadManager
import me.maxos.stalker.remains.utils.Scheduler
import me.maxos.stalker.remains.utils.debug.Debuger

import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class Remains : JavaPlugin() {

	object FileName {
		const val SETTINGS_FILE_NAME = "settings.yml"
	}

	object CommandName {
		const val MAIN_COMMAND = "remains"
	}

	private lateinit var settings: FileManager
	private lateinit var configManager: ConfigManager

	private lateinit var executor: Executor
	private lateinit var tabCompleter: TabCompleter

	private lateinit var deadBodyStorage: DeadBodyStorage
	private lateinit var standCreator: StandCreator
	private lateinit var deadBodyTimer: DeadBodyTimer
	private lateinit var deadBodyManager: DeadBodyManager

	private lateinit var clickOnRemainsEvent: ClickOnRemainsEvent
	private lateinit var closeInventoryEvent: CloseInventoryEvent
	private lateinit var deathEvent: DeathEvent
	private lateinit var quitEvent: QuitEvent

	private lateinit var itemHeadManager: ItemHeadManager

	private lateinit var dataBaseManager: DataBaseManager
	private lateinit var dataBaseSaving: DataBaseSaving

	private lateinit var uuidData: UuidData
	private lateinit var dataSaving: DataSaving

	override fun onEnable() {

		Scheduler.initialization(this)

		settings = FileManager(this, SETTINGS_FILE_NAME)
		configManager = ConfigManager(settings)
		uuidData = UuidData(this)

		initDebuger(configManager.differentConfig?.debugStatus ?: false)

		itemHeadManager = ItemHeadManager(configManager.remainsItemsConfig)

		deadBodyStorage = DeadBodyStorage()
		standCreator = StandCreator(
			deadBodyStorage,
			configManager.inventoryConfig,
			itemHeadManager,
			configManager.standConfig
		)

		deadBodyTimer = DeadBodyTimer(configManager.cleaningConfig)
		deadBodyManager = DeadBodyManager(deadBodyStorage, standCreator, deadBodyTimer, uuidData)

		dataBaseManager = DataBaseManager(this, deadBodyManager)
		dataBaseSaving = DataBaseSaving(
			dataBaseManager, deadBodyManager,
			configManager.differentConfig?.dbSaveDelay ?: 600
		)

		dataSaving = DataSaving(
			deadBodyManager,
			configManager.differentConfig?.dataSaveDelay ?: 600
		)

		// Инициализация и регистрация всех ивентов
		clickOnRemainsEvent = ClickOnRemainsEvent(deadBodyManager).apply { register() }
		closeInventoryEvent = CloseInventoryEvent(deadBodyManager).apply { register() }
		deathEvent = DeathEvent(deadBodyManager).apply { register() }
		quitEvent = QuitEvent(deadBodyManager).apply { register() }

		// Инициализация и установка исполнителя с табуляцией для команды
		executor = Executor(deadBodyManager, dataBaseManager)
		tabCompleter = TabCompleter()
		val cmd = this.getCommand(MAIN_COMMAND)
		cmd?.apply {
			setExecutor(this@Remains.executor)
			tabCompleter = this@Remains.tabCompleter
		}
	}

	private fun initDebuger(boolean: Boolean) {
		Debuger.debugStatus = boolean
	}

	private fun forceSave() {
		dataBaseSaving.forceSaving()
		dataSaving.forceSaving()
		deadBodyManager.clear()
	}

	// Функция-расширение для быстрой регистрации моих ивентов
	private fun Listener.register() {
		val manager = Bukkit.getPluginManager()
		manager.registerEvents(this, this@Remains)
	}

	override fun onDisable() {
		forceSave()
	}

}

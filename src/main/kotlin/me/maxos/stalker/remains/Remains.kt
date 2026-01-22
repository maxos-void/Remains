package me.maxos.stalker.remains

import me.maxos.stalker.remains.Remains.CommandName.MAIN_COMMAND
import me.maxos.stalker.remains.Remains.FileName.SETTINGS_FILE_NAME
import me.maxos.stalker.remains.command.Executor
import me.maxos.stalker.remains.deadbody.management.StandCreator
import me.maxos.stalker.remains.deadbody.management.DeadBodyManager
import me.maxos.stalker.remains.deadbody.management.DeadBodyStorage
import me.maxos.stalker.remains.events.ClickOnRemainsEvent
import me.maxos.stalker.remains.events.CloseInventoryEvent
import me.maxos.stalker.remains.file.FileManager
import me.maxos.stalker.remains.file.config.ConfigManager

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

	private lateinit var deadBodyStorage: DeadBodyStorage
	private lateinit var standCreator: StandCreator
	private lateinit var deadBodyManager: DeadBodyManager

	private lateinit var clickOnRemainsEvent: ClickOnRemainsEvent
	private lateinit var closeInventoryEvent: CloseInventoryEvent

	override fun onEnable() {

		settings = FileManager(this, SETTINGS_FILE_NAME)
		configManager = ConfigManager(settings)

		deadBodyStorage = DeadBodyStorage()
		standCreator = StandCreator(deadBodyStorage)
		deadBodyManager = DeadBodyManager(deadBodyStorage, standCreator)

		executor = Executor(deadBodyManager)

		// Инициализация и регистрация всех ивентов
		clickOnRemainsEvent = ClickOnRemainsEvent(deadBodyManager).apply { register() }
		closeInventoryEvent = CloseInventoryEvent(deadBodyManager).apply { register() }

		// Инициализация и установка исполнителя с табуляцией для команды
		val cmd = this.getCommand(MAIN_COMMAND)
		cmd?.setExecutor(executor)
	}

	override fun onDisable() {
		// Plugin shutdown logic
	}

	// Функция-расширение для быстрой регистрации моих ивентов
	private fun Listener.register() {
		val manager = Bukkit.getPluginManager()
		manager.registerEvents(this, this@Remains)
	}
}

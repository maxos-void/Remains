package me.maxos.stalker.remains.file.config

import me.maxos.stalker.remains.file.config.model.ItemModel
import me.maxos.stalker.remains.utils.debug.Debuger.sendDebug
import me.maxos.stalker.remains.file.FileManager
import me.maxos.stalker.remains.file.config.model.CleaningConfig
import me.maxos.stalker.remains.file.config.model.DifferentConfig
import me.maxos.stalker.remains.file.config.model.InventoryConfig
import me.maxos.stalker.remains.file.config.model.RemainsItemsConfig
import me.maxos.stalker.remains.file.config.model.StandConfig
import me.maxos.stalker.remains.utils.logError
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import java.lang.Exception


class ConfigManager(
	private val settings: FileManager
) {

	companion object RootSectionNames {
		const val REMAINS_ITEMS_SECTION_NAME = "remains-items"
		const val ITEMS_LIST_SECTION_NAME = "items"
		const val DEFAULT_MATERIAL_NAME = "CHEST"

		const val INVENTORY_SECTION_NAME = "inventory"
		const val DEFAULT_INVENTORY_TITLE = "§0Труп игрока {player-name}"
		const val DEFAULT_INVENTORY_SIZE = 45

		const val CLEANING_SECTION_NAME = "cleaning-settings"

		const val STAND_SECTION_NAME = "stand-settings"
		const val DIFFERENT_SECTION_NAME = "different-settings"
	}

	fun reload() {
		settings.reloadConfig()
		initConfig()
	}

	var config: FileConfiguration? = null
		private set
	var remainsItemsConfig: RemainsItemsConfig? = null
		private set
	var inventoryConfig: InventoryConfig? = null
		private set
	var cleaningConfig: CleaningConfig? = null
		private set
	var standConfig: StandConfig? = null
		private set
	var differentConfig: DifferentConfig? = null
		private set

	init { initConfig() }

	private fun initConfig() {
		config = settings.getConfig()
		if (!isNullableConfig()) {
			initRemainsItemsConfig()
			initInventoryConfig()
			initCleaningConfig()
			initStandConfig()
			initDifferentConfig()
		}
	}

	private fun initDifferentConfig() {
		val section = getRequiredSection(DIFFERENT_SECTION_NAME) ?: return

		val debugStatus = section.getBoolean("debug")
		val dbSaveDelay = section.getInt("db-save-delay")
		val dataSaveDelay = section.getInt("data-save-delay")

		differentConfig = DifferentConfig(debugStatus, dbSaveDelay, dataSaveDelay)
	}

	private fun initStandConfig() {
		val section = getRequiredSection(STAND_SECTION_NAME) ?: return

		val gravity = section.getBoolean("gravity")

		val positionSection = getRequiredSection(section, "position") ?: return
		val x = positionSection.getDouble("x")
		val y = positionSection.getDouble("y")
		val z = positionSection.getDouble("z")

		standConfig = StandConfig(gravity, x, y, z)
	}

	private fun initCleaningConfig() {
		val section = getRequiredSection(CLEANING_SECTION_NAME) ?: return
		val remainsSection = getRequiredSection(section, "remains") ?: return
		val sleeperSection = getRequiredSection(section, "sleeper") ?: return

		cleaningConfig = CleaningConfig(
			remainsSection.getBoolean("isTimer"),
			sleeperSection.getBoolean("isTimer"),
			remainsSection.getInt("timer"),
			sleeperSection.getInt("timer")
		)

	}

	private fun initInventoryConfig() {
		val section = getRequiredSection(INVENTORY_SECTION_NAME) ?: return
		val size = section.getString("size")?.toIntOrNull() ?: DEFAULT_INVENTORY_SIZE
		val titleRemain = section.getString("title-remain") ?: DEFAULT_INVENTORY_TITLE
		val titleSleeper = section.getString("title-sleeper") ?: DEFAULT_INVENTORY_TITLE
		val isRandom = section.getBoolean("random")
		inventoryConfig = InventoryConfig(size, titleRemain, titleSleeper, isRandom)
	}

	private fun initRemainsItemsConfig() {

		val section = getRequiredSection(REMAINS_ITEMS_SECTION_NAME) ?: return

		val isRandom = section.getBoolean("random")
		val itemsSection = getRequiredSection(section, ITEMS_LIST_SECTION_NAME) ?: return
		val itemsModels = initItemsModels(itemsSection, isRandom)

		remainsItemsConfig = RemainsItemsConfig(isRandom, itemsModels)
	}

	private fun initItemsModels(itemsSection: ConfigurationSection, isRandom: Boolean): List<ItemModel> {
		return itemsSection.getKeys(false).let {
			// если рандом выключен, значит в список добавляется первый предмет
			// в конфиге он помечен как default
			val keys = if (!isRandom) {
				setOf(it.first())
			} else it
			keys.map { sectionName ->
				val section = getRequiredSection(itemsSection, sectionName)
					?: return emptyList()
				createItemModel(section)
			}
		}
	}

	private fun createItemModel(section: ConfigurationSection): ItemModel {
		val modelName = section.name
		val materialName = section.getString("material") ?: run {
			logError("Предмету $modelName не задан материал! Применяем стандартный...")
			DEFAULT_MATERIAL_NAME
		}
		val material = try {
			Material.valueOf(materialName)
		} catch (e: Exception) {
			logError("Неизвестный материал $materialName")
			Material.valueOf(DEFAULT_MATERIAL_NAME)
		}
		val customModelData = section.getInt("custom-model-data")

		return ItemModel(modelName, material, customModelData)
	}

	private fun getRequiredSection(parentSection: ConfigurationSection, name: String): ConfigurationSection? {
		val section = parentSection.getConfigurationSection(name)
		if (section == null) {
			logError("Секция конфига $name повреждена!")
			return null
		}
		return section
	}
	private fun getRequiredSection(name: String): ConfigurationSection? {
		val section = config!!.getConfigurationSection(name)
		if (section == null) {
			logError("Секция конфига $name повреждена!")
			return null
		}
		return section
	}

	private fun isNullableConfig(): Boolean {
		when (config == null) {
			true -> {
				sendDebug("Конфигурация была повреждена! (null)")
				return true
			}
			false -> return false
		}
	}
}
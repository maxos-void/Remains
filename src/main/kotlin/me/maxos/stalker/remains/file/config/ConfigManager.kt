package me.maxos.stalker.remains.file.config

import me.maxos.stalker.remains.file.config.model.ItemModel
import me.maxos.stalker.remains.debug.sendDebug
import me.maxos.stalker.remains.file.FileManager
import me.maxos.stalker.remains.file.config.model.InventoryConfig
import me.maxos.stalker.remains.file.config.model.RemainsItemsConfig
import me.maxos.stalker.remains.utils.logError
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import java.lang.Exception


const val REMAINS_ITEMS_SECTION_NAME = "remains-items"
const val ITEMS_LIST_SECTION_NAME = "items"
const val DEFAULT_MATERIAL_NAME = "CHEST"

const val INVENTORY_SECTION_NAME = "inventory"
const val DEFAULT_INVENTORY_TITLE = "§0Труп игрока {player-name}"
const val DEFAULT_INVENTORY_SIZE = 45

class ConfigManager(
	private val settings: FileManager
) {

	fun reload() {
		settings.reloadConfig()
		initConfig()
	}

	fun getItemsConfig() = remainsItemsConfig
	fun getInventoryConfig() = inventoryConfig

	private var config: FileConfiguration? = null
	private var remainsItemsConfig: RemainsItemsConfig? = null
	private var inventoryConfig: InventoryConfig? = null

	init { initConfig() }

	private fun initConfig() {
		config = settings.getConfig()
		if (!isNullableConfig()) {
			initRemainsItemsConfig()
			initInventoryConfig()
		}
	}

	private fun initInventoryConfig() {
		val section = getRequiredSection(INVENTORY_SECTION_NAME) ?: return
		val size = section.getString("size")?.toIntOrNull() ?: DEFAULT_INVENTORY_SIZE
		val title = section.getString("title") ?: DEFAULT_INVENTORY_TITLE
		val isRandom = section.getBoolean("random")
		inventoryConfig = InventoryConfig(size, title, isRandom)
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
				val section = getRequiredSection(itemsSection, sectionName) ?: return emptyList()
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
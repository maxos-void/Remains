package me.maxos.stalker.remains.file

import me.maxos.stalker.remains.Remains
import me.maxos.stalker.remains.utils.database.JsonSerializer
import me.maxos.stalker.remains.utils.debug.Debuger
import org.bukkit.Location
import java.io.File
import java.util.UUID

class UuidData(
	private val plugin: Remains
) {
	private val dataFolder = plugin.dataFolder
	private val dataFile = File(dataFolder, "uuids.json")

	init {
		ensureFileExists()
	}

	private fun ensureFileExists() {
		if (!dataFolder.exists()) {
			dataFolder.mkdirs()
		}
		if (!dataFile.exists()) {
			dataFile.createNewFile()
			dataFile.writeText("[]")  // Пустой JSON массив
			Debuger.sendDebug("Создан новый файл uuids.json")
		}
	}

	fun saveUuids(uuids: Map<UUID, Location>) {
		try {
			val json = JsonSerializer.serializationRemains(uuids)
			dataFile.writeText(json)
			Debuger.sendDebug("Сохранено ${uuids.size} UUID в файл")
		} catch (e: Exception) {
			plugin.logger.severe("Ошибка сохранения UUID: ${e.message}")
			throw e
		}
	}

	fun loadUuids(): Map<UUID, Location> {
		return try {
			if (!dataFile.exists() || dataFile.length() == 0L) {
				Debuger.sendDebug("Файл uuids.json пуст или не существует")
				return emptyMap()
			}

			val json = dataFile.readText()
			val uuids = JsonSerializer.getRemains(json) ?: return emptyMap()
			Debuger.sendDebug("Загружено ${uuids?.size} UUID из файла")
			clearUuids()

			return uuids

		} catch (e: Exception) {
			plugin.logger.severe("Ошибка загрузки UUID: ${e.message}")
			emptyMap()  // Возвращаем пустой список вместо выброса исключения
		}
	}

	fun clearUuids() {
		dataFile.writeText("[]")
		Debuger.sendDebug("Файл uuids.json очищен")
	}

}
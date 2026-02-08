package me.maxos.stalker.remains.utils.database

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.maxos.stalker.remains.utils.LiteLocation
import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.UUID

object JsonSerializer {

	val gson = Gson()

	@Throws(IllegalStateException::class)
	fun serializationRemains(map: Map<UUID, Location>): String {
		try {
			return gson.toJson(
				map.mapValues { (_, loc) ->
					LiteLocation(
						loc.world.name,
						loc.x,
						loc.y,
						loc.z,
						loc.yaw,
						loc.pitch
					)
				}
			)
		} catch (e: Exception) {
			throw IllegalStateException("Не удалось сериализовать список UUID стендов", e)
		}
	}

	fun getRemains(string: String): Map<UUID, Location>? {
		return deserializationRemains(string).mapValues { (_, liteLoc) ->
			Location(
				Bukkit.getWorld(liteLoc.world) ?: return null,
				liteLoc.x,
				liteLoc.y,
				liteLoc.z,
				0F,
				liteLoc.yaw
			)
		}
	}

	@Throws(IllegalStateException::class)
	fun deserializationRemains(string: String): HashMap<UUID, LiteLocation> {
		try {
			val type = object : TypeToken<HashMap<UUID, LiteLocation>>() {}.type
			return gson.fromJson(string, type)
		} catch (e: Exception) {
			throw IllegalStateException("Не удалось сериализовать список UUID стендов", e)
		}
	}


	@Throws(IllegalStateException::class)
	fun serializationUuids(uuids: List<UUID>): String {
		try {
			return gson.toJson(uuids)
		} catch (e: Exception) {
			throw IllegalStateException("Не удалось сериализовать список UUID стендов", e)
		}
	}

	@Throws(IllegalStateException::class)
	fun deserializationUuids(string: String): List<UUID> {
		try {
			val type = object : TypeToken<List<UUID>>() {}.type
			return gson.fromJson(string, type)
		} catch (e: Exception) {
			throw IllegalStateException("Не удалось получить список сохранённых UUID стендов", e)
		}
	}
}
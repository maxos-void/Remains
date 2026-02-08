package me.maxos.stalker.remains.utils.database

import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Base64

object Base64Utils {

	@Throws(IllegalStateException::class)
	fun toInventoryBase64(inventory: Inventory): String {
		try {
			val outputStream = ByteArrayOutputStream()
			val dataOutput = BukkitObjectOutputStream(outputStream)
			val invSize = inventory.size

			dataOutput.writeInt(invSize)

			for (i in 0 until invSize) {
				dataOutput.writeObject(inventory.getItem(i))
			}

			dataOutput.close()
			return Base64Coder.encodeLines(outputStream.toByteArray())
		} catch (e: Exception) {
			throw IllegalStateException("Не удалось сохранить ItemStacks", e)
		}
	}

	@Throws(IOException::class)
	fun fromInventoryBase64(data: String): Inventory {
		try {
			val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(data))
			val dataInput = BukkitObjectInputStream(inputStream)
			val inventory = Bukkit.getServer().createInventory(null, dataInput.readInt())

			val invSize = inventory.size

			for (i in 0 until invSize) {
				inventory.setItem(i, dataInput.readObject() as ItemStack?)
			}

			dataInput.close()
			return inventory
		} catch (e: ClassNotFoundException) {
			throw IOException("Не удается расшифровать тип класса", e)
		}
	}

	@Throws(IOException::class)
	fun toItemBase64(item: ItemStack): String {
		ByteArrayOutputStream().use { outputStream ->
			BukkitObjectOutputStream(outputStream).use { dataOutput ->
				dataOutput.writeObject(item.serializeAsBytes())
			}
			return Base64.getEncoder().encodeToString(outputStream.toByteArray())
		}
	}

	@Throws(IOException::class)
	fun fromItemBase64(base64: String): ItemStack {
		return try {
			val data = Base64.getDecoder().decode(base64)
			ByteArrayInputStream(data).use { inputStream ->
				BukkitObjectInputStream(inputStream).use { dataInput ->
					val bytes = dataInput.readObject() as ByteArray
					ItemStack.deserializeBytes(bytes)
				}
			}
		} catch (e: Exception) {
			throw IOException("Не удалось десереализовать предмет", e)
		}
	}

}
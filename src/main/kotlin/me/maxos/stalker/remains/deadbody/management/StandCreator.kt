package me.maxos.stalker.remains.deadbody.management

import me.maxos.stalker.remains.deadbody.model.BodyStand
import me.maxos.stalker.remains.deadbody.model.DeadBody
import me.maxos.stalker.remains.deadbody.model.Sleeper
import me.maxos.stalker.remains.file.config.model.InventoryConfig
import me.maxos.stalker.remains.file.config.model.StandConfig
import me.maxos.stalker.remains.item.ItemHeadManager
import me.maxos.stalker.remains.utils.debug.Debuger.sendDebug
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*


class StandCreator(
	private val storage: DeadBodyStorage,
	private val inventoryConfig: InventoryConfig?,
	private val itemHeadManager: ItemHeadManager,
	private val standConfig: StandConfig?
) {

	fun createIsDb(sleeper: Sleeper): DeadBody? {
		val location = sleeper.location ?: return null
		val world = location.world ?: return null
		val chunk = location.chunk

		chunk.load()
		chunk.isForceLoaded = true

		val searchStand = (world.getEntity(sleeper.standId) as? ArmorStand)
			.also { searchingStand ->
				searchingStand ?: return@also // если НЕ нулл, то выводим дебаг ниже
				sendDebug("Стенд ${searchingStand.uniqueId} обнаружен в мире $world! Пересоздание не требуется")
			} ?: world.spawnEntity(location.apply { pitch = 0F }, EntityType.ARMOR_STAND)
				.toArmorStand()?.apply {
					modify(sleeper.standHeadItem)
				}
				.also { freshStand ->
					freshStand ?: return@also // если НЕ нулл, то выводим дебаг ниже
					sendDebug("Для слиппера ${sleeper.standId} был создан новый стенд! (старый не обнаружен)")
					location.clone().getNearbyEntities(5.0, 5.0, 5.0).filter {
						it.type == EntityType.ARMOR_STAND
					}.forEach {
						sendDebug("РЯДОМ ОБНАРУЖЕН СТЕНД ${it.uniqueId}")
						it.remove()
					}

				} ?: run { // если ВСЁ нулл, то выводим дебаг ниже и возвращаем этот нулл
					sendDebug("Создание стенда для слиппера ${sleeper.standId} завершено неудачно! (null)")
					return null
				}

		return save(
			playerName = sleeper.playerName,
			unloadInventory = sleeper.inventory,
			standId = searchStand.uniqueId,
			bodyStand = BodyStand(
				searchStand, searchStand.uniqueId,
				sleeper.standHeadItem
			),
			isSleeper = true
		)
	}

	fun createIsPlayer(player: Player, drops: List<ItemStack>, isSleeper: Boolean): DeadBody? {
		val world = player.world
		val location = player.location.clone().apply { pitch = 0F }
		// создаём сущность стенда и настраиваем его аттрибуты
		val stand = world.spawnEntity(
			location, EntityType.ARMOR_STAND
		).toArmorStand()?.apply { modify(itemHeadManager.getItem()) } ?: return null
		// создаём наш собственный объект "подставки под тело" на основе армор-стенда
		val standId = stand.uniqueId
		val bodyStand = BodyStand(stand, standId, itemHeadManager.getItem())
		// сохраняем труп в мапу
		return save(
			player = player, drops = drops, standId = standId,
			bodyStand = bodyStand, isSleeper = isSleeper
		)
	}

	private fun save(
		player: Player? = null,
		playerName: String? = null,
		playerId: UUID? = if (playerName != null) Bukkit.getOfflinePlayer(playerName).uniqueId else null,
		unloadInventory: Inventory? = null,
		drops: List<ItemStack>? = null,
		standId: UUID,
		bodyStand: BodyStand,
		isSleeper: Boolean = false
	): DeadBody? {
		val deadBody = DeadBody(
			bodyStand,
			playerId = player?.uniqueId ?: playerId ?: return null,
			playerName = player?.name ?: playerName ?: return null,
			unloadInventory = unloadInventory,
			items = drops,
			isInventoryRandom = inventoryConfig?.isRandom ?: false,
			isSleeper = isSleeper,
			config = inventoryConfig ?: return null
		)
		storage.saveDeadBody(
			standId, deadBody
		)
		return deadBody
	}


	private fun ArmorStand.modify(item: ItemStack? = null) {
		this.isInvisible = true

		standConfig?.let { cfg ->
			this.setGravity(cfg.gravity)
			if (cfg.isChangedPosition) {
				this.location.clone().also {
					this.teleport(
						Location(
							it.world,
							it.x + (standConfig.x),
							it.y + (standConfig.y),
							it.z + (standConfig.z),
						)
					)
				}
			}
		}

		this.isInvulnerable = true
		this.isPersistent = true
		this.isCollidable = true
		this.isMarker = false
		this.setHelmet(item ?: ItemStack(Material.DIAMOND_BLOCK))
		//	this.persistentDataContainer.set(pdc, PersistentDataType.STRING, "tree")
	}

	private fun Entity.toArmorStand(): ArmorStand? {
		return this as? ArmorStand
	}
}
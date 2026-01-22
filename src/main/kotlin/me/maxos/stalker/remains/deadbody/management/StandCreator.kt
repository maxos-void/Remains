package me.maxos.stalker.remains.deadbody.management

import me.maxos.stalker.remains.deadbody.model.BodyStand
import me.maxos.stalker.remains.deadbody.model.DeadBody
import me.maxos.stalker.remains.utils.InventoryUtils.getAllItems
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.UUID

class StandCreator(
	private val storage: DeadBodyStorage
) {

	fun create(player: Player) {
		val world = player.world
		val location = player.location
		// создаём сущность стенда и настраиваем его аттрибуты
		val stand = world.spawnEntity(
			location, EntityType.ARMOR_STAND
		).toArmorStand()?.apply { modify() } ?: return
		// создаём наш собственный объект "подставки под тело" на основе армор-стенда
		val standId = stand.uniqueId
		val bodyStand = BodyStand(stand, standId)
		// сохраняем труп в мапу
		save(player, standId, bodyStand)
	}

	private fun save(player: Player, standId: UUID, bodyStand: BodyStand) {
		storage.saveDeadBody(
			standId, DeadBody(
				bodyStand,
				player.uniqueId,
				player.name,
				player.inventory.getAllItems()
			)
		)
	}


	private fun ArmorStand.modify() {
		this.isInvisible = true
		//	this.setGravity(false)
		this.isInvulnerable = true
		this.isPersistent = true
		this.isCollidable = true
		this.isMarker = false
		this.setHelmet(ItemStack(Material.DIAMOND_BLOCK))
		//	this.persistentDataContainer.set(pdc, PersistentDataType.STRING, "tree")
	}

	private fun Entity.toArmorStand(): ArmorStand? {
		return this as? ArmorStand
	}
}
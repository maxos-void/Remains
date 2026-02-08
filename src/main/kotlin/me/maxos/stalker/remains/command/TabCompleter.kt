package me.maxos.stalker.remains.command

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class TabCompleter: TabCompleter {

	private val list = mutableListOf(
		"spawn",
		"remove"
	)

	override fun onTabComplete(
		sender: CommandSender,
		cmd: Command,
		str: String,
		args: Array<out String>?
	): List<String?>? {
		if (args?.size == 1) {
			return list
		}
		return null
	}
}
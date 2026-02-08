package me.maxos.stalker.remains.data

enum class Type(val operation: String) {
	SYNC("Синхроное сохранение"),
	ASYNC("Асинхронное сохранение")
}
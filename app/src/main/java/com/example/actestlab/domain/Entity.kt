package com.example.actestlab.domain

enum class EntityType { PLAYER, NPC, OBJECT, UNKNOWN }

data class Entity(
    val id: String,
    val type: EntityType,
    val worldX: Float,
    val worldY: Float,
    val width: Float,
    val height: Float,
    val distance: Float,
    val visible: Boolean
)

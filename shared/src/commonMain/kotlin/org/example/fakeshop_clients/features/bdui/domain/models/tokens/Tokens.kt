package org.example.fakeshop_clients.features.bdui.domain.models.tokens

import kotlinx.serialization.Serializable

@Serializable
enum class Spacing { sm, md, lg, xl }

@Serializable
enum class TextStyle { title, subtitle, body, caption }

@Serializable
enum class ButtonStyle { primary, secondary, tertiary }

@Serializable
enum class SpacerSize { sm, md, lg, xl }

@Serializable
enum class NodeAlignment { start, center, end }

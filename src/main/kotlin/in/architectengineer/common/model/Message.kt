package `in`.architectengineer.common.model

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val message: String
)
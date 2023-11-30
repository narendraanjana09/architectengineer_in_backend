package `in`.architectengineer.models.responses

import kotlinx.serialization.Serializable

@Serializable
data class SignUpResponse(
    val token: String,
    val message: String
)

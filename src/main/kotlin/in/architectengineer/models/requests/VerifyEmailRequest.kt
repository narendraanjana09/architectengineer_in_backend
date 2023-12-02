package `in`.architectengineer.models.requests

import kotlinx.serialization.Serializable

@Serializable
data class VerifyEmailRequest(
    val id: String,
    val code: String
)
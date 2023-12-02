package `in`.architectengineer.models.requests

import kotlinx.serialization.Serializable

@Serializable
data class ResendVerifyEmailCodeRequest(
    val email: String
)
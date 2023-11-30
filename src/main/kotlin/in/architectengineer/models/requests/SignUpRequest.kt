package `in`.architectengineer.models.requests

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val city: String,
    val email: String,
    val mobile: String,
    val name: String,
    val password: String
)
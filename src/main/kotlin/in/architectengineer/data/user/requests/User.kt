package `in`.architectengineer.data.user.requests

import `in`.architectengineer.data.user.responses.UserResponse
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.util.UUID

data class User(
    val city: String,
    val email: String,
    val mobile: String,
    val name: String,
    val active: Boolean,
    val dateJoined: String,
    val emailVerificationCode: String,
    val role: String,
    @BsonId val id: String = UUID.randomUUID().toString()
) {
    fun toUserResponse(): UserResponse {
        return UserResponse(
            email,id
        )
    }
}
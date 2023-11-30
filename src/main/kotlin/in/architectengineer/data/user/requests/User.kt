package `in`.architectengineer.data.user.requests

import `in`.architectengineer.data.user.responses.UserResponse
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    val city: String,
    val email: String,
    val mobile: String,
    val name: String,
    val password: String,
    val active: Boolean,
    val dateJoined: String,
    val role: String,
    val salt: String,
    @BsonId val id: ObjectId = ObjectId()
) {
    fun toUserResponse(): UserResponse {
        return UserResponse(
            email,password,salt,id.toString()
        )
    }
}
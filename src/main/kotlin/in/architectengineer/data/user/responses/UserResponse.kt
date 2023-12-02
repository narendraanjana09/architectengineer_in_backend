package `in`.architectengineer.data.user.responses

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class UserResponse(
    val email: String,
    val id:String,
)
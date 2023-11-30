package `in`.architectengineer.data.user

import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import `in`.architectengineer.data.user.requests.User
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList

class UserRepositoryImpl(
    private val db: MongoDatabase
): UserRepository {

    private val users = db.getCollection("users", User::class.java)

    override suspend fun insertUser(user: User): Boolean {
        return users.insertOne(user).wasAcknowledged()
    }

    override suspend fun findUserById(id: String): User? {
        val filter = eq(User::id.name,id)
       return users.find(filter, User::class.java).firstOrNull()
    }

    override suspend fun getAllUsers(): List<User> {
        return users.find().toList()
    }

    override suspend fun findUserByEmail(userName: String): User? {
        val filter = eq(User::email.name,userName)
        return users.find(filter, User::class.java).firstOrNull()
    }
    override suspend fun findUserByMobile(mobile: String): User? {
        val filter = eq(User::mobile.name,mobile)
        return users.find(filter, User::class.java).firstOrNull()
    }

}
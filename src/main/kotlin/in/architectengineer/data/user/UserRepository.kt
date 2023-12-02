package `in`.architectengineer.data.user

import `in`.architectengineer.data.user.requests.User

interface UserRepository {
    suspend fun insertUser(user: User): Boolean
    suspend fun updateUser(user: User): Boolean
    suspend fun findUserById(id: String): User?
    suspend fun getAllUsers(): List<User>?
    suspend fun findUserByEmail(userName: String): User?
    suspend fun findUserByMobile(mobile: String): User?
}
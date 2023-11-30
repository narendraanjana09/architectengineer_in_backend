package `in`.architectengineer.data.user

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import `in`.architectengineer.models.requests.LoginRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes(database: MongoDatabase) {
    val db = UserRepositoryImpl(database)
    get("userid/{id}") {
        val id = call.parameters["id"].toString()
        db.findUserById(id)
            ?.let { foundPerson -> call.respond(foundPerson) }
            ?: call.respond(HttpStatusCode.NotFound)
    }
    get("active") {
        call.respond(HttpStatusCode.Accepted,"Active")
    }
    get("username/{username}") {
        val username = call.parameters["username"].toString()
        db.findUserByEmail(username)
            ?.let { foundPerson -> call.respond(foundPerson.toUserResponse()) }
            ?: call.respond(HttpStatusCode.NotFound)
    }
    get("users") {
        db.getAllUsers()
            .let { list -> call.respond(HttpStatusCode.OK,list) }
    }
    post("user") {
        val request = call.receiveNullable<LoginRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val hash = System.currentTimeMillis().toString()
//        val newUser = UserRequest(
//            email = request.username,
//            password = request.password,
//            salt = hash
//        )
//        if(db.insertUser(newUser)){
//            call.respond(HttpStatusCode.Created,newUser.toUserResponse())
//        }else{
//            call.respond(HttpStatusCode.Forbidden,"Error false")
//        }
    }
}
package `in`.architectengineer.plugins

import `in`.architectengineer.data.user.UserRepository
import `in`.architectengineer.routes.publicRoutes
import `in`.architectengineer.routes.signUp
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(userRepository: UserRepository) {
    routing {
        publicRoutes()
        signUp(userRepository)
    }
}

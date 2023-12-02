package `in`.architectengineer.routes

import `in`.architectengineer.common.model.Message
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.publicRoutes() {
    get("/") {
        call.respond(HttpStatusCode.OK,Message("Connected!"))
    }
}
package `in`.architectengineer.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.publicRoutes() {
    get("/") {
        call.respondText("Hello World, this is a public endpoint!")
    }
}
package `in`.architectengineer.plugins

import `in`.architectengineer.firebase.FirebaseUser
import `in`.architectengineer.firebase.firebase
import io.ktor.server.application.*
import io.ktor.server.auth.*

fun Application.configureFirebaseAuth() {
    install(Authentication) {
        firebase {
            validate {
                FirebaseUser(it.uid, it.name.orEmpty())
            }
        }
    }
}
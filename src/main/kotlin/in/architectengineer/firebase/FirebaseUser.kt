package `in`.architectengineer.firebase

import io.ktor.server.auth.Principal

data class FirebaseUser(val userId: String = "", val displayName: String = "") : Principal
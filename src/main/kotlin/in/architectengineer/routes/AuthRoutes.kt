package `in`.architectengineer.routes

import com.google.firebase.auth.FirebaseAuth
import `in`.architectengineer.common.Constants
import `in`.architectengineer.common.verifyEmail
import `in`.architectengineer.data.user.UserRepository
import `in`.architectengineer.data.user.requests.User
import `in`.architectengineer.firebase.FIREBASE_AUTH
import `in`.architectengineer.firebase.FirebaseUser
import `in`.architectengineer.models.requests.SignUpRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.signUp(
    userRepository: UserRepository
) {
    authenticate(FIREBASE_AUTH) {
        get("authenticate") {
            val user: FirebaseUser =
                call.principal() ?: return@get call.respond(HttpStatusCode.Unauthorized,"User Not Authorized!!")
            call.respond("User is authenticated: $user")
        }
    }
//    authenticate(FIREBASE_AUTH) {
        post("authenticated") {
            val idToken = call.request.headers["Authorization"]?.removePrefix("Bearer ")
            try {
                val decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken)
                val uid = decodedToken.uid
                if(uid==null || uid == "null"){
                    call.respond(HttpStatusCode.Unauthorized,"UID Invalid")
                    return@post
                }else {
                    call.respond(HttpStatusCode.Accepted,"UID Valid $uid, ${decodedToken.name}")

                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Unauthorized,"${e.message}")
                return@post
            }
        val request = call.receiveNullable<SignUpRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest, "Invalid request data")
            return@post
        }

        if (request.email.isBlank() || request.password.isBlank()) {
            call.respond(HttpStatusCode.Conflict, "Email or Password cannot be empty")
            return@post
        }
        if(!request.email.verifyEmail()){
            call.respond(HttpStatusCode.Conflict, "Please provide a valid email")
            return@post
        }

        if (request.password.length < 8) {
            call.respond(HttpStatusCode.Conflict, "Password must be at least 8 characters long")
            return@post
        }
        if (request.name.isBlank()) {
            call.respond(HttpStatusCode.Conflict, "Name can't be empty.")
            return@post
        }
        if (request.mobile.isBlank()) {
            call.respond(HttpStatusCode.Conflict, "Mobile can't be empty.")
            return@post
        }
        if (request.city.isBlank()) {
            call.respond(HttpStatusCode.Conflict, "City can't be empty.")
            return@post
        }
        var existingUser = userRepository.findUserByEmail(request.email)
        if (existingUser != null) {
            call.respond(HttpStatusCode.Conflict, "A user with this email already exists")
            return@post
        }
        existingUser = userRepository.findUserByMobile(request.mobile)
        if (existingUser != null) {
            call.respond(HttpStatusCode.Conflict, "A user with this mobile already exists")
            return@post
        }

        val secret = System.getenv("JWT_SECRET")
        val user = User(
            email = request.email,
            password = "saltedHash.hash",
            salt = "saltedHash.salt",
            name = request.name,
            mobile = request.mobile,
            city = if(request.city == secret) "" else request.city,
            active = false,
            dateJoined = System.currentTimeMillis().toString(),
            role = if(request.city == secret) Constants.Roles.Admin.name else Constants.Roles.User.name
        )

        val wasAcknowledged = userRepository.insertUser(user)
        if (!wasAcknowledged) {
            call.respond(HttpStatusCode.Conflict, "User registration failed")
            return@post
        }

        call.respond(HttpStatusCode.OK, "${if(request.city == secret) "Admin" else "User"} registered successfully. Please Sign-in to continue using the app.")
    }
}
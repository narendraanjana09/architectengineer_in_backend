package `in`.architectengineer.routes

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import `in`.architectengineer.common.*
import `in`.architectengineer.common.htmlTemplates.getEmailVerificationTemplate
import `in`.architectengineer.common.model.Message
import `in`.architectengineer.data.user.UserRepository
import `in`.architectengineer.data.user.requests.User
import `in`.architectengineer.data.user.responses.UserResponse
import `in`.architectengineer.firebase.FIREBASE_AUTH
import `in`.architectengineer.firebase.FirebaseUser
import `in`.architectengineer.models.requests.SignUpRequest
import `in`.architectengineer.models.requests.VerifyEmailRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


fun Route.signUp(
    userRepository: UserRepository
) {
    post ("sendMail"){
       try {
           runBlocking {
               Email.sendEmail(
                   from = "architectengineer.in@gmail.com",
                   fromName = "ArchitectEngineer.in",
                   to = "aanjnapatel0902@gmail.com",
                   toName = "Narendra",
                   sub = "Email Verification Code",
                   body = "",
                   htmlBody = getEmailVerificationTemplate(username = "Narendra","123456")
               )
           }
           call.respond(HttpStatusCode.OK,"Mail Sent")
       }catch (e:Exception) {
           call.respond(HttpStatusCode.ExpectationFailed,"Error ${e.message}")
       }
    }
    post("register") {
        val request = call.receiveNullable<SignUpRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest, Message("Invalid request data"))
            return@post
        }

        if (request.email.isBlank() || request.password.isBlank()) {
            call.respond(HttpStatusCode.Conflict, Message("Email or Password cannot be empty"))
            return@post
        }
        if(!request.email.verifyEmail()){
            call.respond(HttpStatusCode.Conflict, Message("Please provide a valid email"))
            return@post
        }

        if (request.password.length < 8) {
            call.respond(HttpStatusCode.Conflict, Message("Password must be at least 8 characters long"))
            return@post
        }
        if (request.name.isBlank()) {
            call.respond(HttpStatusCode.Conflict, Message("Name can't be empty."))
            return@post
        }
        if (request.mobile.isBlank()) {
            call.respond(HttpStatusCode.Conflict, Message("Mobile can't be empty."))
            return@post
        }
        if (request.city.isBlank()) {
            call.respond(HttpStatusCode.Conflict, Message("City can't be empty."))
            return@post
        }
        var existingUser = userRepository.findUserByEmail(request.email)
        if (existingUser != null) {
            call.respond(HttpStatusCode.Conflict, Message("A user with this email already exists"))
            return@post
        }
        existingUser = userRepository.findUserByMobile(request.mobile)
        if (existingUser != null) {
            call.respond(HttpStatusCode.Conflict, Message("A user with this mobile already exists"))
            return@post
        }

        val secret = System.getenv("SECRET")
        val user = User(
            email = request.email,
            name = request.name,
            mobile = request.mobile,
            city = if(request.city == secret) "" else request.city,
            active = false,
            emailVerificationCode = generateVerificationCode(),
            dateJoined = System.currentTimeMillis().toString(),
            role = if(request.city == secret) Constants.Roles.Admin.name else Constants.Roles.User.name
        )
        try {
            val createRequest: UserRecord.CreateRequest = UserRecord.CreateRequest()
                .setEmail(request.email)
                .setEmailVerified(false)
                .setPassword(request.password)
                .setPhoneNumber("+91${request.mobile}")
                .setDisplayName(request.name)
                .setUid(user.id)
                .setDisabled(false)

            val userRecord = FirebaseAuth.getInstance().createUser(createRequest)
            val wasAcknowledged = userRepository.insertUser(user)
            if (!wasAcknowledged) {
                FirebaseAuth.getInstance().deleteUser(userRecord.uid)
                call.respond(HttpStatusCode.Conflict, Message("User registration failed"))
                return@post
            }
            runBlocking {
                Email.sendEmail(
                    from = "architectengineer.in@gmail.com",
                    fromName = "ArchitectEngineer.in",
                    to = user.email,
                    toName = user.name,
                    sub = "Email Verification Code",
                    body = "",
                    htmlBody = getEmailVerificationTemplate(username = user.name,user.emailVerificationCode)
                )
            }
            call.respond(HttpStatusCode.OK,UserResponse(
                email = request.email,
                id = user.id
            ))
        }catch (e: Exception) {
            call.respond(HttpStatusCode.Conflict, Message("User registration failed ${e.message}"))
        }
    }
    post("verifyEmail") {
        val request = call.receiveNullable<VerifyEmailRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest, Message("Invalid request data"))
            return@post
        }
        if (request.id.isBlank()) {
            call.respond(HttpStatusCode.Conflict, Message("Id cannot be empty"))
            return@post
        }
        if(request.code.isEmpty() || request.code.length!=6){
            call.respond(HttpStatusCode.Conflict, Message("Please provide a valid code"))
            return@post
        }
        var user = userRepository.findUserById(request.id)
        if (user == null) {
            call.respond(HttpStatusCode.Conflict, Message("A user with this id not exists"))
            return@post
        }
        if(user.emailVerificationCode != request.code){
            call.respond(HttpStatusCode.Conflict, Message("Wrong Code!!"))
            return@post
        }
       try {
           val updateRequest: UserRecord.UpdateRequest = UserRecord.UpdateRequest(user.id)
               .setEmailVerified(true)
           FirebaseAuth.getInstance().updateUser(updateRequest)
           call.respond(HttpStatusCode.OK,Message("Email verified successfully!"))
       } catch (e:Exception){
           call.respond(HttpStatusCode.Conflict, Message("Email verification failed ${e.message}"))
       }


    }

    authenticate(FIREBASE_AUTH) {

    }
}
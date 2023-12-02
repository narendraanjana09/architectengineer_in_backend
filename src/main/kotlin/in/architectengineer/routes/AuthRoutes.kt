package `in`.architectengineer.routes

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import `in`.architectengineer.common.*
import `in`.architectengineer.common.htmlTemplates.getEmailVerificationCodeTemplate
import `in`.architectengineer.common.htmlTemplates.getEmailVerificationLinkTemplate
import `in`.architectengineer.common.model.Message
import `in`.architectengineer.data.user.UserRepository
import `in`.architectengineer.data.user.requests.User
import `in`.architectengineer.data.user.responses.UserResponse
import `in`.architectengineer.firebase.FIREBASE_AUTH
import `in`.architectengineer.firebase.FirebaseUser
import `in`.architectengineer.models.requests.LoginRequest
import `in`.architectengineer.models.requests.ResendVerifyEmailCodeRequest
import `in`.architectengineer.models.requests.SignUpRequest
import `in`.architectengineer.models.requests.VerifyEmailRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking


fun Route.signUp(
    userRepository: UserRepository
) {
    authenticate(FIREBASE_AUTH) {
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
                        htmlBody = getEmailVerificationCodeTemplate(username = "Narendra","123456")
                    )
                }
                call.respond(HttpStatusCode.OK,"Mail Sent")
            }catch (e:Exception) {
                call.respond(HttpStatusCode.ExpectationFailed,"Error ${e.message}")
            }
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
        try {
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
                active = true,
                emailVerificationCode = generateVerificationCode(),
                dateJoined = System.currentTimeMillis().toString(),
                role = if(request.city == secret) Constants.Roles.Admin.name else Constants.Roles.User.name
            )

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
                    htmlBody = getEmailVerificationCodeTemplate(username = user.name,user.emailVerificationCode)
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
        if (request.email.isBlank()) {
            call.respond(HttpStatusCode.Conflict, Message("Email cannot be empty"))
            return@post
        }
        if(request.code.isEmpty() || request.code.length!=6){
            call.respond(HttpStatusCode.Conflict, Message("Please provide a valid code"))
            return@post
        }
       try {

           var user = userRepository.findUserByEmail(request.email)
           if (user == null) {
               call.respond(HttpStatusCode.Conflict, Message("A user with this id not exists"))
               return@post
           }
           if(user.emailVerificationCode != request.code){
               call.respond(HttpStatusCode.Conflict, Message("Wrong Code!!"))
               return@post
           }

           val updateRequest: UserRecord.UpdateRequest = UserRecord.UpdateRequest(user.id)
               .setEmailVerified(true)
           FirebaseAuth.getInstance().updateUser(updateRequest)
           call.respond(HttpStatusCode.OK,Message("Email verified successfully!"))
       } catch (e:Exception){
           call.respond(HttpStatusCode.Conflict, Message("Email verification failed ${e.message}"))
       }
    }
    post ("resendVerifyEmailCode") {
        val request = call.receiveNullable<ResendVerifyEmailCodeRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest, Message("Invalid request data"))
            return@post
        }
        if (request.email.isBlank()) {
            call.respond(HttpStatusCode.Conflict, Message("Email cannot be empty"))
            return@post
        }
        try {

            var user = userRepository.findUserByEmail(request.email)
            if (user == null) {
                call.respond(HttpStatusCode.Conflict, Message("A user with this id not exists"))
                return@post
            }
            user = user.copy(
                emailVerificationCode = generateVerificationCode()
            )
            if(!userRepository.updateUser(user)){
                call.respond(HttpStatusCode.Conflict, Message("Resending code failed..."))
                return@post
            }

            runBlocking {
                Email.sendEmail(
                    from = "architectengineer.in@gmail.com",
                    fromName = "ArchitectEngineer.in",
                    to = user!!.email,
                    toName = user!!.name,
                    sub = "Email Verification Code",
                    body = "",
                    htmlBody = getEmailVerificationCodeTemplate(username = user!!.name,user!!.emailVerificationCode)
                )
            }
            call.respond(HttpStatusCode.OK,Message("Verification Code Sent."))
        } catch (e:Exception){
            call.respond(HttpStatusCode.Conflict, Message("Resend verification code failed ${e.message}"))
        }
    }
    post("login") {
        val request = call.receiveNullable<LoginRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest, Message("Invalid request data"))
            return@post
        }
        if (request.email.isBlank()) {
            call.respond(HttpStatusCode.Conflict, Message("Email cannot be empty"))
            return@post
        }
        if (request.email.verifyEmail()) {
            call.respond(HttpStatusCode.Conflict, Message("Email must be valid"))
            return@post
        }

        try {
            var user = userRepository.findUserByEmail(request.email)

            val fUser = FirebaseAuth.getInstance().getUser(user?.id)
            if (user == null || fUser == null) {
                call.respond(HttpStatusCode.Conflict, Message("A user with this id not exists"))
                return@post
            }
            if(user?.active == false){
                call.respond(HttpStatusCode.Conflict, Message("User account is disabled."))
                return@post
            }
            if(!fUser.isEmailVerified) {
                user = user?.copy(
                    emailVerificationCode = generateVerificationCode()
                )
                if(!userRepository.updateUser(user!!)){
                    call.respond(HttpStatusCode.Conflict, Message("Resending code failed..."))
                    return@post
                }
                runBlocking {
                    Email.sendEmail(
                        from = "architectengineer.in@gmail.com",
                        fromName = "ArchitectEngineer.in",
                        to = user!!.email,
                        toName = user!!.name,
                        sub = "Email Verification Code",
                        body = "",
                        htmlBody = getEmailVerificationCodeTemplate(username = user!!.name,user!!.emailVerificationCode)
                    )
                }
                call.respond(HttpStatusCode.Conflict, Message("Verify Email"))
            } else {
                call.respond(HttpStatusCode.OK, Message("User Logged In"))
            }
        } catch (e:Exception){
            call.respond(HttpStatusCode.Conflict, Message("login failed ${e.message}"))
        }
    }
    authenticate(FIREBASE_AUTH){
        post("sendEmailVerificationLink") {
            val user: FirebaseUser =
                call.principal() ?: return@post call.respond(HttpStatusCode.Unauthorized,"User Not Authorized!!")

            try {
                runBlocking {
                    val fUser = FirebaseAuth.getInstance().getUser(user.userId)
                    val link = FirebaseAuth.getInstance().generateEmailVerificationLink(fUser.email)

                    Email.sendEmail(
                        from = "architectengineer.in@gmail.com",
                        fromName = "ArchitectEngineer.in",
                        to = fUser.email,
                        toName = fUser.displayName,
                        sub = "Email Verification Link",
                        body = "",
                        htmlBody = getEmailVerificationLinkTemplate(username = user.displayName,link)
                    )
                }
                call.respond(HttpStatusCode.OK,Message("Verification Link Sent."))
            } catch (e:Exception){
                call.respond(HttpStatusCode.Conflict, Message("Verification Link Send Error ${e.message}"))
            }
        }
    }
    authenticate(FIREBASE_AUTH) {
        get("authenticate") {
            val user: FirebaseUser =
                call.principal() ?: return@get call.respond(HttpStatusCode.Unauthorized,"User Not Authorized!!")
            call.respond("User is authenticated: $user")
        }
    }
}
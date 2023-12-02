package `in`.architectengineer.common

import kotlin.random.Random

fun String.verifyEmail():Boolean{
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
    return this.matches(emailRegex.toRegex())
}
fun generateVerificationCode(): String {
    return Random.nextInt(100000, 999999).toString()
}
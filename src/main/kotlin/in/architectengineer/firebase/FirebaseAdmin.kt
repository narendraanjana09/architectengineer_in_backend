package `in`.architectengineer.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.io.FileInputStream
import java.io.InputStream

object FirebaseAdmin {
    private val refreshToken = FileInputStream("src/main/resources/serviceKey.json")

    private val options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(refreshToken))
        .build()
    fun init(): FirebaseApp = FirebaseApp.initializeApp(options)
}
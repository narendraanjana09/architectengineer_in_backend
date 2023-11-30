package `in`.architectengineer

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.ServerApi
import com.mongodb.ServerApiVersion
import com.mongodb.kotlin.client.coroutine.MongoClient
import `in`.architectengineer.common.Constants
import `in`.architectengineer.data.user.UserRepositoryImpl
import `in`.architectengineer.firebase.FirebaseAdmin
import `in`.architectengineer.plugins.configureFirebaseAuth
import `in`.architectengineer.plugins.configureMonitoring
import `in`.architectengineer.plugins.configureRouting
import `in`.architectengineer.plugins.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}


fun Application.module() {

    val mongoClient =  setupConnection()
    val database = mongoClient.getDatabase(Constants.MAIN_DATABASE)
    val userDataSource = UserRepositoryImpl(database)

    FirebaseAdmin.init()
    configureFirebaseAuth()
    configureRouting(userDataSource)
    configureSerialization()
    configureMonitoring()
}

fun setupConnection(
    connectionEnvVariable: String = "MONGODB_URI"
):MongoClient {
    val connectionString = System.getenv(connectionEnvVariable)
    val serverApi = ServerApi.builder()
        .version(ServerApiVersion.V1)
        .build()
    val mongoClientSettings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(connectionString))
        .serverApi(serverApi)
        .build()
    return MongoClient.create(mongoClientSettings)
}


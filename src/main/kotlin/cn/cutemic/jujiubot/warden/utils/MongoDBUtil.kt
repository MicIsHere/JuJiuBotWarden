package cn.cutemic.jujiubot.warden.utils

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.ServerApi
import com.mongodb.ServerApiVersion
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase


object MongoDBUtil {

    var mongoClient: MongoClient? = null

    private val uri = "mongodb://192.168.100.220"

    private val settings: MongoClientSettings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(uri))
        .build()

    fun connect(): Boolean{
        try {
            mongoClient = MongoClients.create(settings)
        } catch (e: Exception){
            e.printStackTrace()
            return false
        }
        return true
    }

    fun getDatabase(databaseName: String): MongoDatabase?{
        try {
            return mongoClient!!.getDatabase(databaseName)
        }catch (e: Exception){
            e.printStackTrace()
            return null
        }
    }

}
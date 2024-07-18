package cn.cutemic.jujiubot.warden.utils

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase


object MongoDBUtil {
    lateinit var mongoClient: MongoClient

    private val uri = "mongodb://192.168.100.220"
    private val settings: MongoClientSettings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(uri))
        .build()

    fun connect(): Boolean{
        try {
            mongoClient = MongoClient.create(settings)
        } catch (e: Exception){
            e.printStackTrace()
            return false
        }
        return true
    }

    fun getDatabase(databaseName: String): MongoDatabase? {
        try {
            return mongoClient.getDatabase(databaseName)
        }catch (e: Exception){
            e.printStackTrace()
            return null
        }
    }

}
package cn.cutemic.jujiubot.warden.task

import cn.cutemic.jujiubot.warden.utils.MongoDBUtil
import com.mongodb.client.MongoDatabase
import org.bson.Document
import kotlin.system.measureTimeMillis

class ClearBadWords {

    private val dataBase = MongoDBUtil.getDatabase("PallasBot")

    private val badWords = arrayOf(
        "傻逼",
        "操",
        "几把",
        "弱智",
        "逼",
        "脑残",
        "你妈"
    )

    private val cqCode = arrayOf(
        "[CQ:image,",
        "[CQ:record,",
        "[CQ:video,",
        "[CQ:json,",
        "[CQ:forward,",
        "[CQ:at,"
    )

    init {
        println("任务 ClearBadWords 已开始")

        val clearedData = getClearedData(dataBase)

//        clearedData.forEach {
//
//        }

        clearedData.forEach { doc ->
            val answers = doc.first["answers"] as List<Document> // 获取 answers 列表

            answers.forEach { answer ->
                val messages = answer["messages"] as List<String> // 获取 messages 列表
                val keywords = answer["keywords"].toString() // 获取 keywords 字段

                // 检查 messages 是否包含 badWords
                messages.forEach { message ->
                    if (badWords.any { badWord -> message.contains(badWord) }) {
                        println("Found bad word in message: $message")
                    }
                }

                // 检查 keywords 是否包含 badWords
                if (badWords.any { badWord -> keywords.contains(badWord) }) {
                    println("Found bad word in keywords: $keywords")
                }
            }
        }
    }

    private fun getClearedData(database: MongoDatabase?): ArrayList<Pair<Document,String>> {
        val clearedData = ArrayList<Pair<Document,String>>()
        val time = measureTimeMillis {

            database?.let { db ->
                val contextCollection = db.getCollection("context")

                contextCollection.find().forEach { doc ->
                    val keywords = doc["keywords"].toString().trim()
                    val id = doc["_id"].toString()

                    val shouldKeep = cqCode.none { cqCode ->
                        keywords.startsWith(cqCode, ignoreCase = true)
                    }

                    if (shouldKeep) {
                        clearedData.add(Pair(doc, id))
                    }
                }
            }

        }
        println("已清理 context 中的CQ码，耗时 $time ms")
        return clearedData
    }

}
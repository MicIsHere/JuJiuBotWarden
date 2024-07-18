package cn.cutemic.jujiubot.warden.task

import cn.cutemic.jujiubot.warden.utils.MongoDBUtil
import com.mongodb.client.MongoDatabase
import org.bson.Document
import javax.print.Doc
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
        "你妈",
        "sb"
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

        getClearedDataMap(dataBase).forEach { doc ->
            val answers = doc.key["answers"] as List<Document> // 获取 answers 列表

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

    private fun getClearedDataMap(database: MongoDatabase?): Map<Document, String> {
        return database!!.getCollection("context").find()
            .filter { doc ->
                val keywords = doc["keywords"].toString().trim()
                cqCode.none { cqCode ->
                    keywords.startsWith(cqCode, ignoreCase = true)
                }
            }
            .associateWith { doc -> doc["_id"].toString() }
    }
}


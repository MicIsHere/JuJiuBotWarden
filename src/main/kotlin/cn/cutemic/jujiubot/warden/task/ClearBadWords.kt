package cn.cutemic.jujiubot.warden.task

import cn.cutemic.jujiubot.warden.data.Context
import cn.cutemic.jujiubot.warden.utils.MongoDBUtil
import com.mongodb.client.MongoDatabase
import org.bson.Document
import javax.print.Doc
import kotlin.system.measureTimeMillis

class ClearBadWords {

    private val dataBase = MongoDBUtil.getDatabase("PallasBot")!!

    private val badWords = arrayOf(
        "傻逼",
        "操",
        "几把",
        "弱智",
        "逼",
        "脑残",
        "你妈",
        "sb",
        "脑瘫",
        "死吗",
        "死妈",
        "全家",
        "鸡巴",
        "jb",
        "cnm"
    )

    private val cqCode = arrayOf(
        "[CQ:image,",
        "[CQ:record,",
        "[CQ:video,",
        "[CQ:json,",
        "[CQ:forward,",
        "[CQ:at,",
        "[CQ:markdown,",
        "[CQ:face,"
    )

    init {
        println("任务 ClearBadWords 已开始")

        var count = 0

        getClearedDataMap(dataBase).forEach { doc ->
            val answers = doc.key.answers

            answers.forEach { answer ->
                val messages = answer.messages
                val keywords = answer.keywords

                messages
                    .filter { message ->
                        cqCode.none { cqCode ->
                            message.startsWith(cqCode)
                        }
                    }
                    .forEach { message ->
                        if (badWords.any { badWord -> message.contains(badWord) }) {
                            count++
                            println("在 messages 检查到不雅词汇: $message")
                        }
                    }

//                cqCode.forEach {
//                    if (!keywords.startsWith(it)){
//                        if (badWords.any { badWord -> keywords.contains(badWord) }) {
//                            count++
//                            println("在 keywords 搜索到不雅词汇: $keywords")
//                        }
//                    }
//                }

            }
        }
        println("共检查到 $count 个不雅词汇")
    }

    private fun getClearedDataMap(database: MongoDatabase): Map<Context, String> {
        return database.getCollection("context", Context::class.java).find()
            .filter { doc ->
                val keywords = doc.keywords.trim()
                cqCode.none { cqCode ->
                    keywords.startsWith(cqCode)
                }
            }
            .associateWith { doc ->
                // TODO: doc["_id"].toString()
                ""
            }
    }
}


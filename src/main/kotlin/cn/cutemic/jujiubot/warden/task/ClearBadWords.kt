package cn.cutemic.jujiubot.warden.task

import cn.cutemic.jujiubot.warden.data.Context
import cn.cutemic.jujiubot.warden.utils.MongoDBUtil
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectBigLists
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime

class ClearBadWords {

    private val dataBase = MongoDBUtil.getDatabase("PallasBot")!!

    private val badWords = listOf(
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

    private val cqCode = listOf(
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

        runBlocking {
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

                    if (cqCode.none { keywords.startsWith(it) }) {
                        if (badWords.any { badWord -> keywords.contains(badWord) }) {
                            count++
                            println("在 keywords 搜索到不雅词汇: $keywords")
                        }
                    }
                }
            }
        }
        println("共检查到 $count 个不雅词汇")
    }

    private suspend fun getClearedDataMap(database: MongoDatabase): Map<Context, String> {
        val linkedHashMap: Map<Context, String>
        val time = measureTimeMillis {
            linkedHashMap = ObjectArrayList(database.getCollection<Context>("context").find().toList())
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
        println("Scan context used ${time}ms")
        return Object2ObjectLinkedOpenHashMap(linkedHashMap)
    }
}


package cn.cutemic.jujiubot.warden.task.impl

import cn.cutemic.jujiubot.warden.data.Context
import cn.cutemic.jujiubot.warden.utils.Logger
import cn.cutemic.jujiubot.warden.utils.MongoDBUtil
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.bson.Document
import kotlin.system.measureTimeMillis

class ClearBadWords {

    private val dataBase = MongoDBUtil.getDatabase("PallasBot")!!
    private val contextDatabase = dataBase.getCollection<Context>("context")
    private var contextDatabaseSize = 0

    private val badWords = listOf(
        "傻逼",
        "几把",
        "弱智",
        "脑残",
        "你妈",
        "他妈",
        "脑瘫",
        "死吗",
        "死妈",
        "鸡巴",
        "jb",
        "cnm",
        "草泥马",
        "fw",
        "废物",
        "狗屎",
        "强奸",
        "家暴",
        "革命",
        "反动",
        "该消息只支持",
        "请使用新版",
        "做爱",
        "[CQ:at,",
        "sb"
    )

    private val cqCode = listOf(
        "[CQ:image,",
        "[CQ:record,",
        "[CQ:video,",
        "[CQ:json,",
        "[CQ:forward,",
        "[CQ:markdown,",
        "[CQ:face,"
    )

    init {
        Logger.info("任务 ClearBadWords 已开始")

        var count = 0

        runBlocking {
            contextDatabaseSize = getContextDatabaseList()
            getClearedDataMap().forEach { doc ->
                if (badWords.any { badWord -> doc.key.keywords.contains(badWord) }) {
                    val filter = Document("time", doc.key.time)
                    contextDatabase.deleteMany(filter)
                    count++
                    Logger.info("已在 context - message 中删除数据: ${doc.key.keywords}")
                }

                val answers = doc.key.answers

                answers.forEach { answer ->
                    val messages = answer.messages
                    val keywords = answer.keywords
                    val time = answer.time

                    messages
                        .filter { message ->
                            cqCode.none { cqCode ->
                                message.startsWith(cqCode)
                            }
                        }
                        .forEach { message ->
                            if (badWords.any { badWord -> message.contains(badWord) }) {
                                val filter = Document("answers.messages", message)
                                val update = Document("\$pullAll", Document("answers.$[].messages", listOf(message)))
                                contextDatabase.updateMany(filter, update)
                                count++
                                Logger.info("已在 answers - message 中删除数据: $message")
                            }
                        }

                    if (cqCode.none { keywords.startsWith(it) }) {
                        if (badWords.any { badWord -> keywords.contains(badWord) }) {
                            val filter = Document() // 确定要更新的文档的筛选条件
                            val update = Document("\$pull", Document("answers", Document("time", time))) // 使用 $pull 操作符删除匹配的数据
                            contextDatabase.updateMany(filter, update)
                            count++
                            Logger.info("已在 answers - keywords 中删除数据: $keywords -> $time")
                        }
                    }
                }
            }
        }

        Logger.info("任务 ClearBadWords 已完成")
        Logger.info("原始数据共有 $contextDatabaseSize 条，删除了 $count 条数据")
        Logger.info("应剩余 ${contextDatabaseSize - count} 条数据")
    }

    private suspend fun getContextDatabaseList(): Int {
         var count = 0
         contextDatabase.find().toList().forEach {
             count += it.answers.size
         }
        return count
    }
    private suspend fun getClearedDataMap(): Map<Context, String> {
        val linkedHashMap: Map<Context, String>
        val time = measureTimeMillis {
            linkedHashMap = ObjectArrayList(contextDatabase.find().toList())
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
        Logger.info("从 context 中过滤了CQ码，花费了 $time ms")
        return Object2ObjectLinkedOpenHashMap(linkedHashMap)
    }
}


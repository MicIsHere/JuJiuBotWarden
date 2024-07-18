package cn.cutemic.jujiubot.warden

import cn.cutemic.jujiubot.warden.task.ClearBadWords
import cn.cutemic.jujiubot.warden.utils.MongoDBUtil

class Warden {

    init {

        println("JuJiuBot-Warden 初始化..")
        if (!MongoDBUtil.connect()) {
            println("数据库连接失败!")
            System.exit(0)
        }

        println("载入任务..")
        ClearBadWords()
    }

}
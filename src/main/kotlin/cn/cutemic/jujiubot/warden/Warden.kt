package cn.cutemic.jujiubot.warden

import cn.cutemic.jujiubot.warden.task.TaskManager
import cn.cutemic.jujiubot.warden.utils.Logger
import cn.cutemic.jujiubot.warden.utils.MongoDBUtil
import kotlin.system.exitProcess

class Warden {

    init {

        Logger.info("程序初始化..")
        if (!MongoDBUtil.connect()) {
            Logger.error("数据库连接失败!")
            exitProcess(0)
        }

        TaskManager.startTask()

    }

}
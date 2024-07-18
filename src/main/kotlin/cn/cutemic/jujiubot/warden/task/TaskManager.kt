package cn.cutemic.jujiubot.warden.task

import cn.cutemic.jujiubot.warden.task.impl.ClearBadWords
import cn.cutemic.jujiubot.warden.utils.Logger

object TaskManager {

    fun startTask(){

        Logger.info("载入任务..")

        ClearBadWords()
    }

}
package cn.cutemic.jujiubot.warden.data

data class Answer(val keywords: String, val group_id: String, val count: Int, val time: Int, val messages: List<String>)

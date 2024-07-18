package cn.cutemic.jujiubot.warden.utils

import java.awt.Color
import java.io.Console

object Logger {

    fun info(message: String){
        println("${ConsoleColors.WHITE}[信息] ${ConsoleColors.RESET}$message")
    }

    fun warn(message: String){
        println("${ConsoleColors.YELLOW}[警告] ${ConsoleColors.RESET}$message")
    }

    fun error(message: String){
        println("${ConsoleColors.RED}[错误] ${ConsoleColors.RESET}$message")
    }

}
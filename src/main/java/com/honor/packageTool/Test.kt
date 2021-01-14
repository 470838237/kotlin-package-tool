package com.honor.packageTool

import com.sun.xml.internal.fastinfoset.util.StringArray

//login -u username -pw password -p platform
//help
//action args
//-pw 密码 -u 用户名 -p 登录平台标识

val actions = arrayOf("login")

fun main(args: Array<String>) {
    println("Hello, World!")
    var ret = checkArgs(args)
    if (!ret) return


}

fun checkArgs(args: Array<String>): Boolean {
    when (args[0]) {
        "login" -> {
            var index = args.indexOf("-u")
            if (index == -1) {
                println("")
            }

        }
        "help" -> {
            println(
                """
                命令行格式:packager actions [options args]
                支持的actions包括:help login
                --------------------------------------------------------------------------------
                help        输出命令使用帮助
                login       登录工具账号
                --------------------------------------------------------------------------------
                options详情如下:
                options         参数描述                 所属action参数               是否必须传入
                -u              登录用户名               login                        是
                -pw             登录密码                 login                        是
                -p              登录平台                 login                        是
            """.trimIndent()
            )
        }
        else -> {
            println("action输入错误,当前支持action列表:${actions.toString()}")
        }
    }


    return true
}
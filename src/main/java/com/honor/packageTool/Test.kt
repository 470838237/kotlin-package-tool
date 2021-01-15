package com.honor.packageTool

import com.honor.common.net.DefaultResponse
import com.honor.common.net.HttpClient
import com.honor.common.net.ParameterMap
import com.honor.common.tools.FileUtils
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

//login -u username -pw password -p platform
//help
//action args
//-pw 密码 -u 用户名 -p 登录平台标识

val actions = arrayOf("login")

fun main(args: Array<String>) {

    println("测试--------")
}
//= runBlocking {
//
//
//
//    var ret = parseArgs(args)
//    launch {
//
//
//    }
//}

fun login(appid: String?, password: String?, platform: String?): String? {
    val params = ParameterMap<String>()
    params["appid"] = appid
    params["password"] = password
    params["platform"] = platform
    println(params.toFormString())
    val client = HttpClient.getDefaultHttpClient()
    var ret = client.postSync(
        "https://platform.qixia.com/package/login",
        params,
        DefaultResponse()
    )
    if (!ret.success()) {
        println("login failure:code=${ret.responseCode}\nmessage:${ret.errorMsg}")
        return null
    }
    var retStr = ret.body().string()
    FileUtils.writeFile(
        "E:\\Users\\usera\\MyApplication\\PackageTool\\login_ret.txt",
        retStr,
        false
    )
    println(retStr)
    return retStr
}

fun getArgumentAndPrintError(args: Array<String>, option: String): String? {
    var index = args.indexOf(option)
    if (index == -1) {
        println("缺少参数$option,查看参数详情命令 packager help")
        return null
    }
    if (index + 1 > args.size - 1) {
        println("缺少参数$option,查看参数详情命令 packager help")
        return null
    }
    return args[index+1]
}

fun parseArgs(args: Array<String>): Boolean {

    when (args[0]) {
        "login" -> {
            var appid = getArgumentAndPrintError(args, "-u")
            var password = getArgumentAndPrintError(args, "-pw")
            var platform = getArgumentAndPrintError(args, "-p")
//            if ()
            
            login(appid, password, platform)
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
                -u              登录appid                login                        是
                -pw             登录密码                 login                        是
                -p              登录平台                 login                        是
                                "android", "ios"
                          
                                
                                
            """.trimIndent()
            )
        }
        else -> {
            println("action输入错误,当前支持action列表:${actions.toString()}")
        }
    }
    return true
}

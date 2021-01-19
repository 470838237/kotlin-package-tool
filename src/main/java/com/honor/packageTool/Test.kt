package com.honor.packageTool

import com.google.gson.Gson
import com.honor.common.net.DefaultResponse
import com.honor.common.net.HttpClient
import com.honor.common.net.ParameterMap
import com.honor.common.tools.FileUtils
import com.honor.entity.ResultLogin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import kotlin.reflect.KProperty1

//login -u username -pw password -p platform
//help
//action args
//-pw 密码 -u 用户名 -p 登录平台标识

val actions = arrayOf("login", "decompile", "compile", "sign", "exit")
var rootPath: String? = null
var resultLoginPath: String? = null
var tempPath: String? = null
var decompileInputApk: String? = null
var decompileOutputDir: String? = null
var compileInputDir: String? = null
var compileOutPutApk: String? = null
var apkToolJar: String? = null
fun main(args: Array<String>) = runBlocking {
    parseArgs(args)
    launch {


    }
}

fun login(appid: String?, password: String?, platform: String?): String? {
    val params = ParameterMap<String>()
    params["appid"] = appid
    params["password"] = password
    params["platform"] = platform
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
    retStr = String(retStr.toByteArray(charset("GBK")), charset("UTF-8"));
//    println(retStr)
    var resultlogin = Gson().fromJson(retStr, ResultLogin::class.java)

    resultlogin?.details?.packages?.forEach {
        println()
        println("it?.appid${it?.appid}")
        println("it?.config?.customKey?.appId${it?.config?.customKey?.appId}")
        it?.config?.pluginList?.forEach { item ->
            println("item?.config?.appId${item?.config?.appId}")
        }

    }

    var write = FileUtils.writeFile(resultLoginPath, retStr, false)

    println("write=$write")
    return retStr
}

fun getArgumentAndPrintError(args: Array<String>, option: String, print: Boolean = true): String? {
    var index = args.indexOf(option)
    if (index == -1) {
        if (print) println("缺少参数$option,查看参数详情命令 packager help")
        return null
    }
    if (index + 1 > args.size - 1) {
        if (print) println("缺少参数$option,查看参数详情命令 packager help")
        return null
    }
    return args[index + 1]
}

fun parseArgs(args: Array<String>): Boolean {
    rootPath = args[0]
    tempPath = rootPath?.plus("data\\temp")
    resultLoginPath = rootPath?.plus("data\\temp\\user\\login_ret.txt")
    apkToolJar = rootPath?.plus("data\\libs\\apktool.jar")
    decompileOutputDir = rootPath?.plus("data\\temp\\decompiles")
    when (args[1]) {
        "login" -> {
            var appid = getArgumentAndPrintError(args, "-appid")
            var password = getArgumentAndPrintError(args, "-password")
            var platform = getArgumentAndPrintError(args, "-platform")
            login(appid, password, platform)
        }
        "exit" -> {
            FileUtils.deleteFile(tempPath)
        }
        "decompile" -> {
           // packager.bat decompile -in  C:\Users\usera\Desktop\unitydemo.apk  -out E:\Users\usera\MyApplication\PackageTool\data\temp\decompiles\unitydemo

            decompileInputApk = getArgumentAndPrintError(args, "-in")
            decompileOutputDir = getArgumentAndPrintError(args, "-out", false) ?: decompileOutputDir
            decompileOutputDir += "\\" + File(decompileInputApk).name.split(".")[0]
            var decompileCommand =
                "java -jar $apkToolJar d $decompileInputApk -f -o $decompileOutputDir";
            println(decompileCommand.runCommand(File(rootPath)))

        }
        "compile" -> {

        }
        "sign" -> {

        }
        "help" -> {
            println(
                """
                命令行格式:packager actions [options args]
                支持的actions包括:help login  decompile  compile sign exit
                --------------------------------------------------------------------------------
                help        输出命令使用帮助
                login       登录工具账号
                exit        退出登录
                decompile   反编译apk
                compile     编译apk
                sign        对apk签名
                --------------------------------------------------------------------------------
                options详情如下:
                options         参数描述                 所属action参数               是否必须传入
                -appid          登录appid                login                        是
                -password       登录密码                 login                        是
                -platform       登录平台                 login                        是
                                "android", "ios"
                -out            apk输出路径              compile                      可选
                                反编译资源输出路径        decompile                    可选
                -in             apk输入路径              decompile                    是
                                编译资源输入路径          compile                      可选   
                -keystore       签名证书路径              sign                         是
                -alias          证书别名                  sign                        是
                -keypass        证书秘钥                  sign                        是  
                                
            """.trimIndent()
            )
        }
        else -> {
            print("action输入错误,当前支持action列表:")
            actions.forEach {
                print("$it\t")
            }
            println()
        }
    }
    return true
}

fun String.runCommandAsyn(workingDir: File) {
    ProcessBuilder(*split(" ").toTypedArray())
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .start()
        .waitFor(60, TimeUnit.MINUTES)
}

fun String.runCommand(workingDir: File): String? {
    try {
        val parts = this.split("\\s".toRegex())
        val proc = ProcessBuilder(*parts.toTypedArray())
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()

      //  proc.waitFor(60, TimeUnit.MINUTES)
        var ret: String? = ""
        proc.inputStream.bufferedReader().forEachLine {
            println(it)
            ret += it+"\n"
        }
        return ret
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }
}
package com.honor.packageTool

import com.google.gson.Gson
import com.honor.common.net.DefaultResponse
import com.honor.common.net.HttpClient
import com.honor.common.net.ParameterMap
import com.honor.common.tools.FileUtils
import com.honor.entity.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

//login -u username -pw password -p platform
//help
//action args
//-pw 密码 -u 用户名 -p 登录平台标识

val actions = arrayOf("login", "config", "decompile", "compile", "sign", "exit")
var rootPath: String? = null
var resultLoginPath: String? = null
var tempPath: String? = null
var decompileInputApk: String? = null
var decompileOutputDir: String? = null
var compileInputDir: String? = null
var compileOutPutApk: String? = null
var apkToolJar: String? = null
var cacheFilePath: String? = null
var caches: ConfigData = ConfigData()
val gson = Gson()
//login:false未登录,true已登录
//Steps

fun main(args: Array<String>) = runBlocking {
    rootPath = args[0]
    tempPath = rootPath?.plus("data\\temp")
    cacheFilePath = rootPath?.plus("data\\temp\\cache\\cache.txt")
    resultLoginPath = rootPath?.plus("data\\temp\\user\\login_ret.txt")
    apkToolJar = rootPath?.plus("data\\libs\\apktool.jar")
    decompileOutputDir = rootPath?.plus("data\\temp\\decompiles")
    val cacheText = FileUtils.readFile(cacheFilePath)
    if (!isTextEmpty(cacheText)) {
        caches = gson.fromJson(cacheText, ConfigData::class.java)
    }
    compileInputDir = caches?.inputApkPath
    if (compileInputDir != null)
        compileOutPutApk =
            "$tempPath\\apk\\${SimpleDateFormat("yyyy-MM-dd").format(Date())}\\${File(
                compileInputDir!!
            ).name}.apk"

    parseArgs(args)
    launch {


    }
}

fun updateConfig() {
    FileUtils.writeFile(cacheFilePath, gson.toJson(caches), false)
}

fun login(appId: String?, password: String?, platform: String?): String? {
    val resultLoginAction = ResultAction<String>()
    var msg: String? = null
    if (appId == null) msg = msg ?: "缺少参数-a"
    if (password == null) msg = msg?.plus("\n缺少参数-pw") ?: "缺少参数-pw"
    if (platform == null) msg = msg?.plus("\n缺少参数-p") ?: "缺少参数-p"
    if (msg != null) {
        resultLoginAction.message = "login failure:$msg"
        resultLoginAction.messageLevel = MESSAGE_ERROR
        resultLoginAction.result = false
        println(gson.toJson(resultLoginAction))
        return null
    }
    val params = ParameterMap<String>()
    params["appid"] = appId
    params["password"] = password
    params["platform"] = platform
    val client = HttpClient.getDefaultHttpClient()
    val ret = client.postSync(
        "https://platform.qixia.com/package/login",
        params,
        DefaultResponse()
    )
    if (!ret.success()) {
        caches?.isLogin = false
        updateConfig()
        resultLoginAction.message = "login failure:code=${ret.responseCode},message:${ret.errorMsg}"
        resultLoginAction.messageLevel = MESSAGE_ERROR
        resultLoginAction.result = false
        println(gson.toJson(resultLoginAction))
        return null
    }

    var retStr = ret.body().string()
    retStr = String(retStr.toByteArray(charset("GBK")), charset("UTF-8"))
    val resultLogin = Gson().fromJson(retStr, ResultLogin::class.java)
    caches?.isLogin = resultLogin.success()
    updateConfig()
    val write = FileUtils.writeFile(resultLoginPath, retStr, false)
    resultLoginAction.ret = retStr
    resultLoginAction.result = resultLogin.success()
    resultLoginAction.messageLevel = if (resultLogin.success()) MESSAGE_NORMAL else MESSAGE_ERROR
    resultLoginAction.message =
        if (resultLogin.success()) "login successful" else "login failure:code=${resultLogin.code},message:${resultLogin.message}"
    var str = gson.toJson(resultLoginAction)
    println(str)
    FileUtils.writeFile(File("temp.txt").absolutePath, str, false)
    return retStr
}

fun getArgument(args: Array<String>, option: String): String? {
    val index = args.indexOf(option)
    if (index == -1) {
        return null
    }
    if (index + 1 > args.size - 1) {
        return null
    }
    return args[index + 1]
}


fun parseArgs(args: Array<String>): Boolean {
    when (args[1]) {
        "login" -> {
            val appid = getArgument(args, "-a")
            val password = getArgument(args, "-pw")
            val platform = getArgument(args, "-p")
            login(appid, password, platform)
        }
        "config" -> {
            var isConfig: Boolean = false
            val resultConfigAction = ResultAction<ConfigData>()
            resultConfigAction.ret = caches
            if (!caches.isLogin) {
                resultConfigAction.result = false
                resultConfigAction.message = "please login before do config action!"
                resultConfigAction.messageLevel = MESSAGE_ERROR
                println(gson.toJson(resultConfigAction))
                return false
            }
            val inputApkPath = getArgument(args, "-iap")

            if (inputApkPath != null) {
                if (File(inputApkPath).exists()) {
                    caches.inputApkPath = inputApkPath
                    isConfig = true
                } else {
                    resultConfigAction.result = false
                    resultConfigAction.message = "-iap 配置的文件不存在"
                    resultConfigAction.messageLevel = MESSAGE_ERROR
                    println(gson.toJson(resultConfigAction))
                    return false
                }
            }

            val ksp = getArgument(args, "-ksp")
            val kpass = getArgument(args, "-kpass")
            val alias = getArgument(args, "-alias")
            var msg: String? = null
            if (ksp == null) msg = msg ?: "缺少参数-ksp"
            if (kpass == null) msg = msg?.plus("\n缺少参数-kpass") ?: "缺少参数-kpass"
            if (alias == null) msg = msg?.plus("\n缺少参数-alias") ?: "缺少参数-alias"
            if (ksp == null && kpass == null && alias == null) msg = null
            if (ksp != null && kpass != null && alias != null) {
                //verify 签名
            }
            if (msg != null) {
                resultConfigAction.result = false
                resultConfigAction.message = msg
                resultConfigAction.messageLevel = MESSAGE_ERROR
                println(gson.toJson(resultConfigAction))
                return false
            }
            updateConfig()
            resultConfigAction.result = true
            resultConfigAction.message = "config successful"
            resultConfigAction.messageLevel = MESSAGE_NORMAL
            println(gson.toJson(resultConfigAction))
        }

        "exit" -> {
            FileUtils.deleteFile(tempPath)
            // updateConfig("login",resultlogin.success())
        }
        "decompile" -> {
            // packager.bat decompile -in  C:\Users\usera\Desktop\unitydemo.apk  -out E:\Users\usera\MyApplication\PackageTool\data\temp\decompiles\unitydemo

            decompileInputApk = getArgument(args, "-in")
            decompileOutputDir = getArgument(args, "-out") ?: decompileOutputDir
            decompileOutputDir += "\\" + File(decompileInputApk!!).name.split(".")[0]
            val decompileCommand =
                "java -jar $apkToolJar d $decompileInputApk -f -o $decompileOutputDir"
            decompileCommand.runCommand()
        }
        "compile" -> {
            val compileInputDirTmp = getArgument(args, "-in")
            val compileOutPutApkTmp = getArgument(args, "-out")
            if (compileInputDirTmp != null && compileOutPutApkTmp == null) {
                compileInputDir = compileInputDirTmp
                compileOutPutApk =
                    "$tempPath\\apk\\${SimpleDateFormat("yyyy-MM-dd").format(Date())}\\${File(
                        compileInputDir!!
                    ).name}.apk"
            } else {
                compileInputDir = compileInputDirTmp ?: compileInputDir
                compileOutPutApk = compileOutPutApkTmp ?: compileOutPutApk
            }
            println("compileOutPutApk=$compileOutPutApk")
            println("compileInputDir=$compileInputDir")
            //packager.bat compile -in  E:\Users\usera\MyApplication\PackageTool\data\temp\decompiles\unitydemo
            File(compileOutPutApk!!).parentFile.mkdirs()
            "java -jar  $apkToolJar b  $compileInputDir -o $compileOutPutApk  -f".runCommand()
        }

        "sign" -> {
            //packager.bat sign -keystore  storepath -alias alias -keypass  keypass

        }
        "help" -> {
            println(
                """
                命令行格式:packager actions [options args]
                支持的actions包括:help login config decompile  compile sign exit
                --------------------------------------------------------------------------------
                help        输出命令使用帮助
                login       登录工具账号
                config      配置参数,可多次调用配置参数，参数将被缓存使用
                exit        退出登录
                decompile   反编译apk
                compile     编译apk
                sign        对apk签名
                --------------------------------------------------------------------------------
                options详情如下:
                options         参数描述                 所属action参数               是否必须传入           调用条件
                -a              登录appid                login                        是                   
                -p              登录密码                 login                        是
                -pw             登录平台                 login                        是
                                "android", "ios"
                -iap            母包文件路径              config                                            login之后
                -ksp            签名证书路径              config                                            login之后
                -alias          证书别名                 config                      需要同ksp一起传入       login之后
                -kpass          证书秘钥                 config                      需要同ksp一起传入       login之后
                                
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

fun String.runCommand(): String? {
    try {
        val parts = this.split("\\s".toRegex())
        val proc = ProcessBuilder(*parts.toTypedArray())
            .directory(File(rootPath!!))
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()

        //  proc.waitFor(60, TimeUnit.MINUTES)
        var ret: String? = ""
        proc.inputStream.bufferedReader().forEachLine {
            println(it)
            ret += it + "\n"
        }
        proc.errorStream?.bufferedReader()?.forEachLine {
            println("error:$it")
            ret += it + "\n"
        }
        return ret
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }
}
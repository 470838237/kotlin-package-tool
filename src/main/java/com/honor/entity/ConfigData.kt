package com.honor.entity

class ConfigData {
    var inputApkPath: String? = null
    var isLogin: Boolean = false
    val keystoreInfo: MutableList<KeyStoreInfo> = mutableListOf()
}


class KeyStoreInfo {
    var storePath: String? = null
    var alias: String? = null
    var storePass: String? = null
    var md5: String? = null
    var storeName: String? = null
}
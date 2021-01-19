package com.honor.entity

import com.google.gson.annotations.SerializedName
import com.honor.common.net.Result

class ResultLogin : Result() {
    var details: DetailsLogin? = null

}

class DetailsLogin {
    @SerializedName("package")
    var packages: List<PackageInfo>? = null
    var plugin: List<PluginInfo>? = null
}

class PackageInfo {
    var appid: Int? = null
    var pkid: Int? = null
    var secret: String? = null
    var name: String? = null
    var version: List<PackageVersion>? = null
    var initdata: InitData? = null
    var config: PackageConfig? = null

    @SerializedName("channel_id")
    var channelId: Int? = null

    @SerializedName("channel_code")
    var channelCode: String? = null

    @SerializedName("channel_name")
    var channelName: String? = null
    var description: String? = null
    var icon: String? = null
    var screen: String? = null
    var horizontal: Boolean? = null
    var bundle: String? = null
}

class PackageVersion {
    var date: String? = null
    var version: String? = null
    var dependency: String? = null
    var md5: String? = null
    var url: String? = null
}

class InitData {
    var eve: String? = null
}

class PackageConfig {
    var clientKey: ClientKey? = null
    var otherKey: List<String>? = null
    var customKey: CustomKey? = null
    var pluginList: List<PackagePluginInfo>? = null
}

class ClientKey {
    var appId: String? = null
}

class CustomKey {
    var appId: String? = null
    var appName: String? = null
}

class PackagePluginInfo {
    @SerializedName("plugin_id")
    var pluginId: Int? = null
    var config: PackagePluginConfig? = null

}

class PackagePluginConfig {
    var appId: String? = null
}

class PluginInfo {
    var id: Int? = null
    var name: String? = null
    var code: String? = null
    var type: String? = null
    var description: String? = null
    var version: List<PluginVersion>? = null
}

class PluginVersion {
    var version: String? = null
    var date: String? = null
    var md5: String? = null
    var url: String? = null

}
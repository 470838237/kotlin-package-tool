package com.honor.entity

const val MESSAGE_NORMAL = 1
const val MESSAGE_WARNING = 2
const val MESSAGE_ERROR = 3
class ResultAction<T> {
    var tip: String = """
        tip:返回字段说明
        messageLevel:消息级别,1表示Normal,2表示Warning,3表示Error
        message:处理程序过程产生的处理信息，可用于界面展示
        onlyMessage:true只有message数据有效，忽略其他数据,false时返回程序处理结果,接收端可以使用返回数据处理业务
        ret:onlyMessage为false时有效，返回处理结果
        result:true此次程序执行成功,false程序执行失败
    """.trimIndent()
    var messageLevel: Int = 1
    var message: String? = null
    var onlyMessage: Boolean = false
    var ret: T? = null
    var result: Boolean = true
}
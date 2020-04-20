package com.cordyho.utils

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

object CordyUtils {

    var httpQueues: RequestQueue? = null

    var application: Context? = null

    var baseUrl = ""   // 后台接口域名等等

    var httpSuccessFieldName = "code" //修改成跟后台约定成功返回代码的字段名字

    var httpSuccessCode = "200" // 修改成跟后台约定成功返回的代码

    var msgFieldName = "" //修改成跟后台约定的消息字段的名字

    var httpTimeOut: Int = 10000  // 请求超时，单位毫秒

    var globalParam = HashMap<String?, String?>()  // 全局头部参数或者其他参数..

    /*必须先实例化，才能使用其他Utils*/
    fun init(context: Context) {
        application = context
        httpQueues = Volley.newRequestQueue(application) //volley队列
    }

    /*构建Volley必要参数*/
    fun initVolley(baseUrl: String = "", httpSuccessFieldName: String, httpSuccessCode: String, msgFieldName: String = "", httpTimeOut: Int = 10000) {
        this.baseUrl = baseUrl
        this.httpSuccessFieldName = httpSuccessFieldName
        this.httpSuccessCode = httpSuccessCode
        this.msgFieldName = msgFieldName
        this.httpTimeOut = httpTimeOut
    }

    fun addVolleyGlobalParam(key: String?, value: String?) { //添加全局请求参数
        globalParam[key] = value
    }
}
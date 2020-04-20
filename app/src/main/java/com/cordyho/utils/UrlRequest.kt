package com.cordyho.utils

import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class UrlRequest {

    private val requestParams = HashMap<String?, String?>()
    private val paramsObject = HashMap<String?, Any?>()
    private var url: String = ""
    private var isLogParam = false

    fun url(url: String): UrlRequest {
        this.url = CordyUtils.baseUrl + url
        return this
    }

    fun noBaseUrl(url: String): UrlRequest {
        this.url = url
        return this
    }

    fun param(key: String?, value: Any?): UrlRequest {
        requestParams[key] = value.toString()
        return this
    }

    fun paramsObject(key: String, value: Any?): UrlRequest {
        paramsObject[key] = value
        return this
    }

    fun printUrl(): UrlRequest {
        println("url： $url")
        return this
    }

    fun printParam(): UrlRequest {
        isLogParam = true
        return this
    }

    //Get请求
    fun getDataFromUrlGet(dataRequestResponse: DataRequestResponse) {
        createUrlParamForGet()
        val stringRequest = object : StringRequest(Method.GET, url, { response ->
            processJsonResponse(response, dataRequestResponse)
        },
                { error -> dataRequestResponse.onRequestFailure(error.toString()) }) { //请求失败(网络原因或接口404)

            override fun getHeaders(): Map<String?, String?> { //传头部
                addGlobalParam()
                logAllParam()
                return requestParams
            }
        }
        stringRequest.tag = url
        /*设置超时时长*/
        stringRequest.retryPolicy = DefaultRetryPolicy(CordyUtils.httpTimeOut, 0, 2f)
        CordyUtils.httpQueues?.add(stringRequest)
    }

    //Post请求
    fun getDataFromUrlPost(dataRequestResponse: DataRequestResponse) {
        val stringRequest = object : StringRequest(Method.POST, url, { response ->
            processJsonResponse(response, dataRequestResponse)
        },
                { error -> dataRequestResponse.onRequestFailure(error.toString()) }) { //请求失败(网络原因或接口404)

            override fun getHeaders(): Map<String?, String?> { //传头部
                addGlobalParam()
                logAllParam()
                return requestParams
            }

            override fun getParams(): HashMap<String?, String?> { //传Post参数
                return requestParams
            }
        }
        stringRequest.tag = url
        /*设置超时时长*/
        stringRequest.retryPolicy = DefaultRetryPolicy(CordyUtils.httpTimeOut, 0, 2f)
        CordyUtils.httpQueues?.add(stringRequest)
    }

    //Post JsonObject 数组请求
    fun getDataFromUrlPostForJsonObject(dataRequestResponse: DataRequestResponse) {   // 请求数组对象，一定要用JsonObjectRequest
        val stringRequest = object : JsonObjectRequest(Method.POST, url, JSONObject(paramsObject), { jsonObject ->
            processJsonResponse(jsonObject.toString(), dataRequestResponse)
        },
                { error -> dataRequestResponse.onRequestFailure(error.toString()) }) { //请求失败(网络原因或接口404)

            override fun getHeaders(): Map<String?, String?> { //传头部
                addGlobalParam()
                logAllParam()
                return requestParams
            }

            override fun getParams(): Map<String?, String?> { //传Post参数
                return requestParams
            }

            override fun getBody(): ByteArray {
                val s = Gson().toJson(paramsObject)
                return s.toByteArray(StandardCharsets.UTF_8)
            }
        }
        stringRequest.tag = url
        /*设置超时时长*/
        stringRequest.retryPolicy = DefaultRetryPolicy(CordyUtils.httpTimeOut, 0, 2f)
        CordyUtils.httpQueues?.add(stringRequest)
    }

    private fun createUrlParamForGet() {
        var paramString = ""
        for (key in requestParams.keys)
            paramString += "$key=${requestParams[key]}&"
        if (paramString.isNotBlank())
            url += "?" + paramString.replaceRange(paramString.lastIndex, paramString.length, "") //替换最后的&号
    }

    private fun logAllParam() {  //打印输出全部参数
        if (isLogParam)
            println("normalParam： $requestParams\nparamsObject： $paramsObject")
    }

    private fun addGlobalParam() {  //添加全局请求参数
        for (i in CordyUtils.globalParam.keys)
            requestParams[i] = CordyUtils.globalParam[i]
    }

    private fun processJsonResponse(response: String, dataRequestResponse: DataRequestResponse) {
        try {
            val jsonObject = JSONObject(response)
            if (jsonObject.getString(CordyUtils.httpSuccessFieldName) == CordyUtils.httpSuccessCode)
                dataRequestResponse.onRequestSuccess(jsonObject.toString(), jsonObject.getString(CordyUtils.msgFieldName))  //code 1请求成功
            else
                dataRequestResponse.onRequestFailure(jsonObject.getString(CordyUtils.msgFieldName))
        } catch (e: Exception) {
            dataRequestResponse.onRequestFailure(response)
        }
    }

    interface DataRequestResponse {

        fun onRequestSuccess(jsonData: String, msg: String)

        fun onRequestFailure(errMsg: String)
    }
}
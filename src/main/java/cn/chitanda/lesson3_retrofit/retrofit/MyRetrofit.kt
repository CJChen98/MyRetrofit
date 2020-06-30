package cn.chitanda.lesson3_retrofit.retrofit

import okhttp3.Call
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import java.lang.RuntimeException
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap

/**
 *@auther: Chen
 *@createTime: 2020/6/29 18:30
 *@description 通过构建者模式对网络请求接口进行封装
 **/
@Suppress("UNCHECKED_CAST")
class MyRetrofit(val callFactory: Call.Factory, val baseUrl: HttpUrl) {

    //存储请求信息
    private val serviceMethodCache: MutableMap<Method, ServiceMethod> = ConcurrentHashMap()

    fun <T> create(service: Class<T>) = Proxy.newProxyInstance(service.classLoader, arrayOf(service)) { _, method, args ->
        val serviceMethod = loadServiceMethod(method)
        serviceMethod.invoke(args)
    } as T


    private fun loadServiceMethod(method: Method) =
            //先从缓存中查找,没有再向缓存中添加
            serviceMethodCache[method] ?: synchronized(serviceMethodCache) {
                ServiceMethod.Builder(this, method).build().also { serviceMethodCache[method] = it }
            }

    /*
      TODO: 构建者模式:将一个复杂对象的构建和它的表示分离,可以使使用者不必知道内部的组成细节
     */
    class Builder {
        private var baseUrl: HttpUrl? = null
        private var callFactory: Call.Factory? = null
        fun baseUrl(baseUrl: String): Builder {
            this.baseUrl = baseUrl.toHttpUrl()
            return this
        }

        fun build(): MyRetrofit {
            baseUrl ?: throw RuntimeException("baseUrl is null")
            callFactory ?: OkHttpClient().also { callFactory = it }
            return MyRetrofit(callFactory!!, baseUrl!!)
        }
    }
}


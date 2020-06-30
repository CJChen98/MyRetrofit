package cn.chitanda.lesson3_retrofit.retrofit


import android.util.Log
import cn.chitanda.lesson3_retrofit.retrofit.annotation.Field
import cn.chitanda.lesson3_retrofit.retrofit.annotation.GET
import cn.chitanda.lesson3_retrofit.retrofit.annotation.POST
import cn.chitanda.lesson3_retrofit.retrofit.annotation.Query
import okhttp3.Call
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.Request
import java.lang.RuntimeException
import java.lang.reflect.Method
import kotlin.properties.Delegates

/**
 *@auther: Chen
 *@createTime: 2020/6/29 18:50
 *@description: 记录请求类型，参数，完整地址等
 **/
class ServiceMethod(builder: Builder) {


    private var callFactory: Call.Factory
    private var relativeUrl: String
    private var hasBody: Boolean
    private var parameterHandlers: List<ParameterHandler>
    private var formBuilder: FormBody.Builder? = null
    private val httpMethod: String
    var baseUrl: HttpUrl
    var urlBuilder: HttpUrl.Builder? = null

    init {
        callFactory = builder.myRetrofit.callFactory
        relativeUrl = builder.relativeUrl
        hasBody = builder.hasBody
        parameterHandlers = builder.parameterHandlers
        baseUrl = builder.myRetrofit.baseUrl
        httpMethod = builder.httpMethod
        // TODO: 2020/6/29   如果有请求体,创建一个okhttp的请求体对象
        if (hasBody) formBuilder = FormBody.Builder()

    }

    fun invoke(args: Array<Any>?): Any? {
        // TODO: 1.处理请求地址与参数
        parameterHandlers.forEachIndexed { index, handler ->
            // TODO: 2020/6/29  handler中本来就记录了key,现在加入相应的value
            handler.apply(this, args?.get(index).toString())
        }
        // TODO: 2020/6/29 2.得到最终请求地址
        if (urlBuilder == null) {
            urlBuilder = baseUrl.newBuilder(relativeUrl)
                    ?: throw RuntimeException("$urlBuilder is null ")
        }
        val url = urlBuilder?.build().also { urlBuilder = null }
        var formBody: FormBody? = null
        if (formBuilder != null) formBody = formBuilder?.build().also { formBuilder = FormBody.Builder() }
        val request = Request.Builder().url(url!!).method(httpMethod, formBody).build()
        return callFactory.newCall(request)
    }

    // TODO: 2020/6/29 POST请求,把K-V拼接到url里面
    fun addFieldParameter(key: String, value: String) {
        formBuilder?.add(key, value)
    }

    // TODO: 2020/6/29 GET请求,把K-V放到请求体中
    fun addQueryParameter(key: String, value: String) {
        if (urlBuilder == null) urlBuilder = baseUrl.newBuilder(relativeUrl)
                ?: throw RuntimeException("$urlBuilder is null ")
        urlBuilder?.addQueryParameter(key, value)
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    class Builder(val myRetrofit: MyRetrofit, method: Method) {
        lateinit var relativeUrl: String
        lateinit var httpMethod: String
        var hasBody by Delegates.notNull<Boolean>()

        //获取方法上的所有的注解
        private val methodAnnotations: Array<Annotation> = method.annotations

        //获得方法参数上的所有的注解 TODO 一个参数可以有多个注解 一个方法可以有多个参数
        private val parameterAnnotations: Array<Array<out Annotation>> = method.parameterAnnotations
        var parameterHandlers = mutableListOf<ParameterHandler>()


        fun build(): ServiceMethod {
            // TODO 1.只解析方法上的注解GET和POST
            methodAnnotations.forEachIndexed { _, annotation ->
                when (annotation) {
                    is POST -> {
                        httpMethod = "POST"
                        relativeUrl = annotation.value
                        hasBody = true
                    }
                    is GET -> {
                        httpMethod = "GET"
                        relativeUrl = annotation.value.also { Log.d("Chen", "build: $it") }
                        hasBody = false
                    }
                }
            }
            // TODO: 2.解析方法菜属的注解
            parameterAnnotations.forEachIndexed { i, annotations ->
                // 处理参数上的每一个注解
                annotations.forEach { annotation ->
                    when (annotation) {
                        is Field -> {
                            if (httpMethod == "GET") throw RuntimeException("$httpMethod can't use annotation ${GET::class.java.name}," +
                                    "please use annotation ${Query::javaClass.name} ")
                            //得到注解上的value:请求参数的key 并储存起来
                            parameterHandlers.add(i, ParameterHandler.FieldParameterHandle(annotation.value))
                        }
                        is Query -> {
                            //得到注解上的value:请求参数的key 并储存起来
                            parameterHandlers.add(i, ParameterHandler.QueryParameterHandler(annotation.value))
//                            parameterHandlers[i] = ParameterHandler.QueryParameterHandler(annotation.value)
                        }
                    }
                }
            }
            return ServiceMethod(this)
        }
    }
}
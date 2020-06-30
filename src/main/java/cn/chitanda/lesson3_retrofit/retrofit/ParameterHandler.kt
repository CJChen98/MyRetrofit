package cn.chitanda.lesson3_retrofit.retrofit

/**
 *@auther: Chen
 *@createTime: 2020/6/29 20:55
 *@description: 记录参数对应请求中的name
 **/
abstract class ParameterHandler {
    //TODO: serviceMethod可以看作回调
    abstract fun apply(serviceMethod: ServiceMethod, value: String)

    class QueryParameterHandler(private val key: String) : ParameterHandler() {

        override fun apply(serviceMethod: ServiceMethod, value: String) {
            serviceMethod.addQueryParameter(key, value)
        }
    }

    class FieldParameterHandle(private val key: String) : ParameterHandler() {
        override fun apply(serviceMethod: ServiceMethod, value: String) {
            serviceMethod.addFieldParameter(key, value)
        }
    }
}
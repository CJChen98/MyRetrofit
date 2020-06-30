package cn.chitanda.lesson3_retrofit.retrofit.annotation

/**
 *@auther: Chen
 *@createTime: 2020/6/29 20:28
 *@description:
 **/
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class GET(val value: String)
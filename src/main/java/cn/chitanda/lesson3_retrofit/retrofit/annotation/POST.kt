package cn.chitanda.lesson3_retrofit.retrofit.annotation

import java.lang.annotation.ElementType

/**
 *@auther: Chen
 *@createTime: 2020/6/29 19:35
 *@description:
 **/
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class POST(val value: String)
package cn.chitanda.lesson3_retrofit.api

import cn.chitanda.lesson3_retrofit.retrofit.annotation.Field
import cn.chitanda.lesson3_retrofit.retrofit.annotation.GET
import cn.chitanda.lesson3_retrofit.retrofit.annotation.POST
import cn.chitanda.lesson3_retrofit.retrofit.annotation.Query
import okhttp3.Call

/**
 *@auther: Chen
 *@createTime: 2020/6/29 22:33
 *@description:
 **/
interface WeatherApi {

    @GET("/find")
    fun getWeather(@Query("location") location: String, @Query("key") key: String): Call

    @POST("/find")
    fun postWeather(@Field("location") location: String, @Field("key") key: String): Call
}
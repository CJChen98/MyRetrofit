package cn.chitanda.lesson3_retrofit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import cn.chitanda.lesson3_retrofit.retrofit.MyRetrofit
import cn.chitanda.lesson3_retrofit.api.WeatherApi
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var retrofit: MyRetrofit
    private lateinit var api: WeatherApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        retrofit = MyRetrofit.Builder().baseUrl("https://search.heweather.net").build()
        api = retrofit.create(WeatherApi::class.java)

        getButton.setOnClickListener {
            get(it)
        }
        postButton.setOnClickListener {
            post(it)
        }
    }

    fun get(view: View) {
        val call = api.getWeather("luo", "fdd88ed3893948f0a64a0b448702e73d")
        call.enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.d("Chen", "onFailure: ")
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    runOnUiThread {
                        textview.text = body
                    }
                    Log.d("Chen", "onResponse: $body")

                }
            }
        })
    }

    fun post(view: View) {
        val call = api.postWeather("luo", "fdd88ed3893948f0a64a0b448702e73d")
        call.enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.d("Chen", "onFailure: ")
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    runOnUiThread {
                        textview.text = body
                    }
                    Log.d("Chen", "onResponse: $body")

                }
            }
        })

    }
}

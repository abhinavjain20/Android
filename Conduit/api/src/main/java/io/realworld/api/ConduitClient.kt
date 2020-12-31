package io.realworld.api

import io.realworld.api.services.ConduitAPI
import io.realworld.api.services.ConduitAuthAPI
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object ConduitClient {

   var authToken: String? = null

    private val authInterceptor = Interceptor { chain ->
        var req = chain.request()
        authToken?.let {
            req = req.newBuilder()
                .addHeader("Authorization", "Token $it")
                .build()
        }
        chain.proceed(req)
    }

    private val okHttpBuilder = OkHttpClient.Builder()
        .readTimeout(5, TimeUnit.SECONDS)
        .callTimeout(5, TimeUnit.SECONDS)
        .connectTimeout(2, TimeUnit.SECONDS)

    private val retrofitBuilder = Retrofit.Builder()
        .baseUrl("http://conduit.productionready.io/api/")
        .addConverterFactory(MoshiConverterFactory.create())

    val publicApi = retrofitBuilder
        .client(okHttpBuilder.build())
        .build()
        .create(ConduitAPI::class.java)

    val authApi = retrofitBuilder
        .client(okHttpBuilder.addInterceptor(authInterceptor).build())
        .build()
        .create(ConduitAuthAPI::class.java)
}
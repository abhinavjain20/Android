package io.realworld.android.data

import io.realworld.api.ConduitClient

object ArticlesRepo {
    private val api = ConduitClient.publicApi
    private val authApi = ConduitClient.authApi

    suspend fun getGlobalFeed() = api.getArticles().body()?.articles
    suspend fun getMyFeed() = authApi.getFeedArticles().body()?.articles
}
package io.realworld.android.data

import io.realworld.api.ConduitClient
import io.realworld.api.models.entities.Article
import io.realworld.api.models.entities.ArticleData
import io.realworld.api.models.requests.CreateArticleRequest

object ArticlesRepo {
    private val api = ConduitClient.publicApi
    private val authApi = ConduitClient.authApi

    suspend fun getGlobalFeed() = api.getArticles().body()?.articles
    suspend fun getMyFeed() = authApi.getFeedArticles().body()?.articles
    suspend fun fetchArticle(slug: String) = api.getArticlebySlug(slug).body()?.article
    suspend fun createArticle(
        body: String,
        description: String,
        title: String
    ): Article? {
        val response = authApi.createArticle(
            CreateArticleRequest(
                ArticleData(
                    body, description, title
                )
            )
        )
        return response.body()?.article
    }
}
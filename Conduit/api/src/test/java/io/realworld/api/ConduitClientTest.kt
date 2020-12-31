package io.realworld.api

import io.realworld.api.models.entities.SignupData
import io.realworld.api.models.requests.SignUpRequest
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.random.Random

class ConduitClientTest {

    private val conduitClient = ConduitClient

    @Test
    fun get_articles() {
        runBlocking {
            val articles = conduitClient.publicApi.getArticles()
            assertNotNull(articles.body()?.articles)
        }
    }

    @Test
    fun get_articles_author() {
        runBlocking {
            val articles = conduitClient.publicApi.getArticles(author = "vasiliy12")
            assertNotNull(articles.body()?.articles)
        }
    }

    @Test
    fun get_srticles_tag() {
        runBlocking {
            val articles = conduitClient.publicApi.getArticles(tag = "dragons")
            assertNotNull(articles.body()?.articles)
        }
    }

    @Test
    fun createUser() {
        val userCreds = SignupData(
            email = "testemail${Random.nextInt(999, 9999)}@test.com",
            password = "pass${Random.nextInt(9999, 9999999)}",
            username = "random_user${Random.nextInt(99, 999)}"
        )
        runBlocking {
            val resp = conduitClient.publicApi.signupUser(SignUpRequest(userCreds))
            assertEquals(userCreds.username,resp.body()?.user?.username)
        }
    }
}
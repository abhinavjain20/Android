package io.realworld.android.data

import io.realworld.api.ConduitClient
import io.realworld.api.models.entities.*
import io.realworld.api.models.requests.CreateArticleRequest
import io.realworld.api.models.requests.LoginRequest
import io.realworld.api.models.requests.SignUpRequest
import io.realworld.api.models.requests.UserUpdateRequest

object UserRepo {
    private val api = ConduitClient.publicApi
    private val authApi = ConduitClient.authApi

    suspend fun login(email: String, password: String): User? {
        val response = api.loginUser(LoginRequest(LoginData(email, password)))

        ConduitClient.authToken = response.body()?.user?.token
        return response.body()?.user
    }

    suspend fun signup(username: String, email: String, password: String): User? {
        val response = api.signupUser(SignUpRequest(SignupData(email, password, username)))

        ConduitClient.authToken = response.body()?.user?.token
        return response.body()?.user
    }

    suspend fun updateUser(
        bio: String?,
        username: String?,
        image: String?,
        email: String?,
        password: String?
    ): User? {
        val response = authApi.updateCurrentUser(
            UserUpdateRequest(
                UserUpdateData(
                    bio, email, image, username, password
                )
            )
        )
        return response.body()?.user
    }

    suspend fun getCurrentUser(token: String): User? {
        ConduitClient.authToken = token
        return authApi.getCurrentUser().body()?.user
    }


    suspend fun getUserProfile() = authApi.getCurrentUser().body()?.user
}
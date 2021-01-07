package io.realworld.android.data

import io.realworld.api.ConduitClient
import io.realworld.api.models.entities.Profile

object ProfileRepo {
    private val authApi = ConduitClient.authApi

    suspend fun followProfile(username: String): Profile? {
        val response = authApi.followProfile(username)
        return response.body()?.profile
    }
}
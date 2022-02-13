package com.devtides.githubrepos.model

import io.reactivex.Single
import retrofit2.http.*

const val apiUrl: String = "https://github.com/login/oauth/access_token"
const val userRepo: String = "user/repos"

interface GithubAPI {
    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST(apiUrl)
    fun getAuthToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String
    ): Single<GithubToken>

    @GET("user/repos")
    fun getAllRepo(): Single<List<GithubRepo>>
}

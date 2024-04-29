package com.fshou.ceritain.data.remote.retrofit

import com.fshou.ceritain.data.remote.response.BaseResponse
import com.fshou.ceritain.data.remote.response.LoginResponse
import com.fshou.ceritain.data.remote.response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): BaseResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse


    @Multipart
    @POST("stories")
    suspend fun postStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Header("Authorization") token: String
    ):  BaseResponse

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String
    ): StoriesResponse

    @GET("stories")
    suspend fun getStoriesWitLocation(
        @Header("Authorization") token: String,
        @Query("location") location : Int = 1,
        ): StoriesResponse


}
package com.zoop.zoopandroidsample.api

import retrofit2.Call
import retrofit2.http.Body

import retrofit2.http.POST
import retrofit2.http.Path

interface LoginService {

    @POST("v1/marketplaces/{id_marketplace}/users/signin")
    fun login(
        @Path("id_marketplace") marketplaceId: String,
        @Body loginRequest: LoginRequest
    ): Call<LoginResponse?>?

}
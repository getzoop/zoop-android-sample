package com.example.zoopclientsample.api

import com.google.gson.annotations.SerializedName

class LoginRequest(
    @SerializedName("username") var username: String,
    @SerializedName("password") var password: String,
    @SerializedName("persist") var persist: Boolean
)
package com.zoop.zoopandroidsample.api

import com.google.gson.annotations.SerializedName

class LoginResponse(
    @SerializedName("id") var id: String,
    @SerializedName("resource") var resource: String,
    @SerializedName("token") var token: String,
    @SerializedName("first_name") var firstName: String,
    @SerializedName("last_name") var lastName: String,
    @SerializedName("username") var username: String,
    @SerializedName("require_email_confirm") var requireEmailConfirm: String,
    @SerializedName("permissions") var permissions: List<Permission>,
    @SerializedName("created_at") var createdAt: Float,
    @SerializedName("updated_at") var updatedAt: String,
    @SerializedName("uri") var uri: String
)

class Permission (
    @SerializedName("marketplace_id") var marketplaceId: String,
    @SerializedName("customer_id") var sellerId: String,
    @SerializedName("type") var type: String,
    @SerializedName("model_name") var modelName: String
)
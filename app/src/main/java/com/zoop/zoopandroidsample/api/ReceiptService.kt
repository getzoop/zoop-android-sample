package com.zoop.zoopandroidsample.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ReceiptService {

    @GET("v1/marketplaces/{id_marketplace}/receipts/{id_receipt}")
    fun getReceipt(
        @Header("Authorization") authorization: String?, @Path(
            "id_marketplace"
        ) marketplaceId: String?, @Path("id_receipt") id_receipt: String?
    ): Call<Any?>?

}
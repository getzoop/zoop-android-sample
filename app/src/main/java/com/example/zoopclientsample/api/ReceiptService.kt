package com.example.zoopclientsample.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ReceiptService {

    @GET("v1/marketplaces/{id_marketplace}/receipts/{id_receipt}")
    fun getReceipt(
        @Header("Authorization") authorization: String?, @Path(
            "id_marketplace"
        ) id_marketplace: String?, @Path("id_receipt") id_receipt: String?
    ): Call<Any?>?

}
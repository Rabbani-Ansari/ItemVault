package com.locae.itemvault.api

import com.locae.itemvault.data.model.Item
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ItemApiService {
    @GET("items")
    suspend fun getItems(): List<Item>

    @POST("items")
    suspend fun postItem(@Body item: Item): Item
}

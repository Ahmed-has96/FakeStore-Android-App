package com.example.fakestore.Api

import com.example.fakestore.model.ProductModelItem
import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface {
    @GET("products")
    fun getProducts(): Call<List<ProductModelItem>>
}
package com.example.fakestore.Api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.fakestore.model.ProductModelItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// ProductRepository.kt
class ProductRepository {
    private val apiInterface: ApiInterface = ApiClient.getClient().create(ApiInterface::class.java)
    private val productsLiveData = MutableLiveData<List<ProductModelItem>>()

    // Function to get the LiveData of products
    fun getProducts(): LiveData<List<ProductModelItem>> {
        return productsLiveData
    }

    // Function to refresh the product list from the API
    fun refreshProducts() {
        apiInterface.getProducts().enqueue(object : Callback<List<ProductModelItem>> {
            override fun onResponse(call: Call<List<ProductModelItem>>, response: Response<List<ProductModelItem>>) {
                if (response.isSuccessful) {
                    productsLiveData.value = response.body()
                }else{
                    Log.e("ProductRepository", "onResponse: ${response.code()}", )
                }
            }

            override fun onFailure(call: Call<List<ProductModelItem>>, t: Throwable) {
                // Handle failure
                t.printStackTrace()
                Log.e("ProductRepository", "onResponse: ${t.message}", )

            }
        })
    }
}

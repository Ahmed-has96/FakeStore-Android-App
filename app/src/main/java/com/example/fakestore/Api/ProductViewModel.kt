package com.example.fakestore.Api

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.fakestore.model.ProductModelItem

// ProductViewModel.kt
class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ProductRepository()

    val products: LiveData<List<ProductModelItem>> = repository.getProducts()
    fun getProducts(){
        repository.refreshProducts()
    }
}

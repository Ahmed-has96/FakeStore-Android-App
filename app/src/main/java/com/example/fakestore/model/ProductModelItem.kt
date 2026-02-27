package com.example.fakestore.model

import java.io.Serializable

data class ProductModelItem(
    val category: String,
    val description: String,
    val id: Int,
    val image: String,
    val price: Double,
    val rating: Rating,
    val title: String,
):Serializable
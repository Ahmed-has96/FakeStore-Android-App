package com.example.fakestore.sql

// ProductEntity.kt


data class ProduktEntitet(
    val id: Long = 0,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    val rate: Double,
    val count: Int,
    var quantity: Int,
    var isFave:Boolean?=null
)

package com.example.fakestore.sql

import java.io.Serializable

class HistorikkEntitet(var id: Long?=null, var created: Long?=null, var productsIds: String?=null, var price: String?=null) :
    Serializable
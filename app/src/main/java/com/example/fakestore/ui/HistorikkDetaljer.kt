package com.example.fakestore.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fakestore.R
import com.example.fakestore.model.ProductModelItem
import com.example.fakestore.model.Rating
import com.example.fakestore.sql.ProduktEntitet
import com.example.fakestore.sql.ProduktSQLiteHjelper
import java.text.SimpleDateFormat
import java.util.Date

class HistorikkDetaljer : AppCompatActivity() {
    lateinit var tvDateTime: TextView
    lateinit var tvPrice: TextView
    lateinit var imgBack: ImageView
    lateinit var recyclerView: RecyclerView

    lateinit var sqLiteHelper: ProduktSQLiteHjelper
    var id: Long? = null
    var products = mutableListOf<ProduktEntitet>()
    lateinit var produktAdapter: ProduktAdapter

    //Initialiserer aktiviteten, setter opp UI og laster detaljer for den valgte historikken.
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_details_actvity)
        window.statusBarColor = ContextCompat.getColor(this, R.color.main)


        tvDateTime = findViewById(R.id.tvDateTime)
        tvPrice = findViewById(R.id.tvPrice)
        imgBack = findViewById(R.id.imgBack)
        recyclerView = findViewById(R.id.rvData);
        id = intent.getLongExtra("id", 0)

        imgBack.setOnClickListener {
            finish()
        }

        sqLiteHelper = ProduktSQLiteHjelper(this)
        val history = sqLiteHelper.getHistory(id ?: 0)
        val cleanIdsString = history?.productsIds?.substring(1, history.productsIds!!.length - 1)
        val idStrings = cleanIdsString?.split(", ")

        val idList = idStrings?.map { it.toLong() }
        idList?.forEachIndexed { i, it ->
            val product = sqLiteHelper.getProductById(it)
            if (product != null) {
                products.add(product)
            };
        }

        // Henter og viser dato og tid for kjøpshistorikken.
        tvDateTime.text = "Date & Time: " + SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss").format(
            Date(history?.created ?: System.currentTimeMillis())
        )


        // Viser den totale prisen og antall elementer i kjøpshistorikken.
        tvPrice.text = "Price: " + history?.price + " ( ${idList?.size} items)"
        recyclerView.layoutManager = LinearLayoutManager(this)
        val productModel = products.map {
            ProductModelItem(
                it.category,
                it.description,
                it.id.toInt(),
                it.image,
                it.price,
                Rating(it.count, it.rate),
                it.title
            )
        }
        produktAdapter = ProduktAdapter(this, false, productModel as MutableList<ProductModelItem>)
        recyclerView.adapter = produktAdapter
    }
}
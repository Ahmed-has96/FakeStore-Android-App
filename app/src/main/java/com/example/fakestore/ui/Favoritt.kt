package com.example.fakestore.ui


import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fakestore.R
import com.example.fakestore.model.ProductModelItem
import com.example.fakestore.model.Rating
import com.example.fakestore.sql.ProduktEntitet
import com.example.fakestore.sql.ProduktSQLiteHjelper

// Favoritt: Aktiviteten som håndterer visning av brukerens favorittprodukter.
class Favoritt : AppCompatActivity() {

    private lateinit var produktSQLiteHjelper: ProduktSQLiteHjelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var history: ImageView
    private lateinit var cart: ImageView
    private lateinit var back: ImageView
    private var list = mutableListOf<ProduktEntitet>()
    private lateinit var produktAdapter: ProduktAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fave)
        window.statusBarColor = ContextCompat.getColor(this, R.color.main)

        initializeViews()
        setupRecyclerView()
        loadFavoriteProducts()
    }

    // Initialiserer UI-elementene og setter klikkhåndterere.
    private fun initializeViews() {
        recyclerView = findViewById(R.id.rvData)
        history = findViewById(R.id.imgList)
        cart = findViewById(R.id.imgCart)
        back = findViewById(R.id.imgBack)

        back.setOnClickListener { finish() }
        history.setOnClickListener { navigateTo(Historikk::class.java) }
        cart.setOnClickListener { navigateTo(Handlekurv::class.java) }
    }

    // Navigerer til en annen aktivitet.
    private fun navigateTo(destination: Class<*>) {
        startActivity(Intent(this, destination))
    }

    // Setter opp RecyclerView med en LinearLayoutManager og tilknytter adapteren.
    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        produktAdapter = ProduktAdapter(this, true, mutableListOf())
        recyclerView.adapter = produktAdapter
    }

    // Laster inn favorittprodukter fra databasen og oppdaterer listen.
    private fun loadFavoriteProducts() {
        produktSQLiteHjelper = ProduktSQLiteHjelper(this)
        updateProductList(produktSQLiteHjelper.getAllfAVProducts(true))
    }

    // Oppdaterer listen med produkter og reflekterer endringene i UI.
    private fun updateProductList(newList: List<ProduktEntitet>) {
        list.clear()
        list.addAll(newList)
        val productModel = list.mapToProductModel()
        produktAdapter.submitData(productModel)
        findViewById<TextView>(R.id.noData).isVisible = list.isEmpty()
    }

    // Mapper ProduktEntitet listen til ProductModelItem for å brukes i adapteren.
    private fun List<ProduktEntitet>.mapToProductModel(): MutableList<ProductModelItem> {
        return this.map {
            ProductModelItem(
                it.category,
                it.description,
                it.id.toInt(),
                it.image,
                it.price,
                Rating(it.count, it.rate),
                it.title
            )
        }.toMutableList()
    }

    override fun onResume() {
        super.onResume()
        updateProductList(produktSQLiteHjelper.getAllfAVProducts(true))
    }
}

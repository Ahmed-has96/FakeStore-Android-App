package com.example.fakestore

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fakestore.Api.ProductViewModel
import com.example.fakestore.model.ProductModelItem
import com.example.fakestore.sql.ProduktEntitet
import com.example.fakestore.sql.ProduktSQLiteHjelper
import com.example.fakestore.ui.Handlekurv
import com.example.fakestore.ui.Favoritt
import com.example.fakestore.ui.Historikk
import com.example.fakestore.ui.ProduktAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var productViewModel: ProductViewModel
    private lateinit var recyclerView: RecyclerView
    private var list = mutableListOf<ProductModelItem>()
    private lateinit var cartBtn: ImageView
    private lateinit var historyBtn: ImageView
    private lateinit var favBtn: ImageView
    private lateinit var sqlSQLiteHelper: ProduktSQLiteHjelper
    private lateinit var etSearch: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        window.statusBarColor = ContextCompat.getColor(this, R.color.main)

        recyclerView = findViewById(R.id.rvData)
        cartBtn = findViewById(R.id.imgCart)
        historyBtn = findViewById(R.id.imgHistory)
        favBtn = findViewById(R.id.imgFav)
        etSearch = findViewById(R.id.etSearch)
        sqlSQLiteHelper = ProduktSQLiteHjelper(this)

        recyclerView.layoutManager = LinearLayoutManager(this)

        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        list.addAll(sqlSQLiteHelper.getAllOfflineProducts())

        val produktAdapter = ProduktAdapter(this, false,list)
        recyclerView.adapter = produktAdapter
        productViewModel.refreshProducts()


        productViewModel.products.observe(this) { products ->
            list.clear()
            list.addAll(products)
            produktAdapter.notifyDataSetChanged()
            list.forEach {
                val entity = ProduktEntitet(
                    it.id.toLong(),
                    it.title,
                    it.price,
                    it.description,
                    it.category,
                    it.image,
                    it.rating.rate,
                    it.rating.count, 1, false
                )
                sqlSQLiteHelper.addProductOffline(entity)
            }
        }

        // Implementerer søkefunksjonalitet.
        etSearch.addTextChangedListener {
            val text = it.toString()
            if (text.isNotEmpty()) {
                val filteredList = list.filter {
                    it.title.lowercase().contains(text.lowercase())
                }
                produktAdapter.submitData(filteredList as MutableList<ProductModelItem>)
            } else {
                produktAdapter.submitData(list as MutableList<ProductModelItem>)

            }
        }

        // Setter opp klikkhåndterere for navigasjonsikoner.
        cartBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, Handlekurv::class.java))
        }
        historyBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, Historikk::class.java))
        }

        favBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, Favoritt::class.java))
        }

    }
}
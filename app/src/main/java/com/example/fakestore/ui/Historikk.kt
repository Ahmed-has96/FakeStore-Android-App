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
import com.example.fakestore.sql.HistorikkEntitet
import com.example.fakestore.sql.ProduktSQLiteHjelper

class Historikk : AppCompatActivity() {
    lateinit var produktSQLiteHjelper: ProduktSQLiteHjelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var imgCart: ImageView
    private lateinit var imgFav: ImageView
    private var list = mutableListOf<HistorikkEntitet>()
    private lateinit var imgBack: ImageView

    // Initialiserer aktiviteten, setter opp UI og laster historikkdata fra SQLite-databasen.
    private lateinit var cartAdapter: HistorikkAdapter
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        window.statusBarColor = ContextCompat.getColor(this, R.color.main)
        recyclerView = findViewById(R.id.rvData)
        imgCart = findViewById(R.id.imgCart)
        imgFav = findViewById(R.id.imgFav)
        imgBack = findViewById(R.id.imgBack)

        imgBack.setOnClickListener {
            finish()
        }
        // Oppretter en instans av SQLite-hjelperen for å hente historikkdata.
        produktSQLiteHjelper = ProduktSQLiteHjelper(this)

        // Legger til all historikk i listen og viser en melding hvis listen er tom.
        list.addAll(produktSQLiteHjelper.getAllHistory())
        findViewById<TextView>(R.id.noData).isVisible = list.isEmpty()

        // Setter opp RecyclerView med en LinearLayoutManager og tilknytter adapteren.
        recyclerView.layoutManager = LinearLayoutManager(this)
        cartAdapter = HistorikkAdapter(this, list, produktSQLiteHjelper)
        recyclerView.adapter = cartAdapter

        // Navigasjonsklikkhåndterere for å gå til Handlekurv- og Favoritt-skjermene.
        imgCart.setOnClickListener {
            startActivity(Intent(this, Handlekurv::class.java))
        }

        imgFav.setOnClickListener {
            startActivity(Intent(this, Favoritt::class.java))
        }
    }
}
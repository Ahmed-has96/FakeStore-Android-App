package com.example.fakestore.ui

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fakestore.R
import com.example.fakestore.sql.HistorikkEntitet
import com.example.fakestore.sql.ProduktEntitet
import com.example.fakestore.sql.ProduktSQLiteHjelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder



class Handlekurv : AppCompatActivity() {
    // Variabler for UI-elementer og databasehjelper.
    private lateinit var produktSQLiteHjelper: ProduktSQLiteHjelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var historyIcon: ImageView
    private lateinit var backIcon: ImageView
    private lateinit var favIcon: ImageView
    private var productList = mutableListOf<ProduktEntitet>()
    private var handlekurvAdapter: HandlekurvAdapter? = null
    private lateinit var checkOutButton: MaterialButton
    private var totalCheckoutAmount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        window.statusBarColor = ContextCompat.getColor(this, R.color.main)

        initializeViews()
        setupRecyclerView()
        loadProductsIntoList()
        setupCheckoutButton()
    }

    // Initialiserer UI-elementene.
    private fun initializeViews() {
        recyclerView = findViewById(R.id.rvData)
        favIcon = findViewById(R.id.imgFav)
        checkOutButton = findViewById(R.id.btnCheckOut)
        historyIcon = findViewById(R.id.imgHistory)
        backIcon = findViewById(R.id.imgBack)

        // Navigasjon for tilbake-knappen.
        backIcon.setOnClickListener { finish() }

        // Navigasjon til Historikk-skjermen.
        historyIcon.setOnClickListener { navigateTo(Historikk::class.java) }

        // Navigasjon til Favoritt-skjermen.
        favIcon.setOnClickListener { navigateTo(Favoritt::class.java) }
    }

    private fun navigateTo(destination: Class<*>) {
        startActivity(Intent(this, destination))
    }

    // Setter opp RecyclerView med en LinearLayoutManager og tilknytter adapteren.
    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        produktSQLiteHjelper = ProduktSQLiteHjelper(this)
        handlekurvAdapter = HandlekurvAdapter(this, productList, ::deleteProduct, ::updateTotalAmount)
        recyclerView.adapter = handlekurvAdapter
    }

    // Laster inn produkter fra databasen til listen og oppdaterer.
    private fun loadProductsIntoList() {
        productList.addAll(produktSQLiteHjelper.getAllProducts())
        findViewById<TextView>(R.id.noData).isVisible = productList.isEmpty()
        updateTotalAmount()
    }

    // Fjerner et produkt fra handlekurven og oppdaterer listen.
    private fun deleteProduct(product: ProduktEntitet) {
        produktSQLiteHjelper.deleteProduct(product.id)
        productList.remove(product)
        handlekurvAdapter?.notifyDataSetChanged()
        updateTotalAmount()
    }

    // Aktiverer fullføring og bekreftelse av ordren når brukeren trykker på den.
    private fun setupCheckoutButton() {
        checkOutButton.setOnClickListener {
            if (productList.isNotEmpty()) showCheckoutConfirmationDialog()
            else Toast.makeText(this, "Handlekurven er tom", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showCheckoutConfirmationDialog() {
        MaterialAlertDialogBuilder(this).create().apply {
            setTitle("Ordre")
            setMessage("Fullfør ordre?")
            setButton(DialogInterface.BUTTON_POSITIVE, "Ja") { dialog, _ -> completeOrder(dialog) }
            setButton(DialogInterface.BUTTON_NEGATIVE, "Nei") { dialog, _ -> dialog.dismiss() }
            show()
        }
    }

    // Fullfører ordren og oppdaterer databasen og UI.
    private fun completeOrder(dialog: DialogInterface) {
        produktSQLiteHjelper.addHistory(createHistoryEntity())
        clearProductsFromCart()
        dialog.dismiss()
        finish()
    }

    // Oppdaterer totalbeløpet for ordren.
    @SuppressLint("SetTextI18n")
    private fun updateTotalAmount() {
        totalCheckoutAmount = productList.sumOf { it.price * it.quantity }.toInt()
        checkOutButton.text = "Fullfør ordre ($$totalCheckoutAmount)"
    }

    // Oppretter en historikkentitet for lagring i databasen.
    private fun createHistoryEntity() = HistorikkEntitet(
        created = System.currentTimeMillis(),
        productsIds = productList.joinToString { it.id.toString() },
        price = "kr $totalCheckoutAmount"
    )

    // Fjerner alle produkter fra handlekurven etter at ordren er fullført.
    private fun clearProductsFromCart() {
        productList.forEach { produktSQLiteHjelper.deleteProduct(it.id) }
        Toast.makeText(this, "Ordre registrert", Toast.LENGTH_SHORT).show()
        productList.clear()
    }
}

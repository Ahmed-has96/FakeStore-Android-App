package com.example.fakestore.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.fakestore.R
import com.example.fakestore.model.ProductModelItem
import com.example.fakestore.sql.ProduktEntitet
import com.example.fakestore.sql.ProduktSQLiteHjelper
import com.google.android.material.button.MaterialButton

class ProduktDetaljer : AppCompatActivity() {
    lateinit var imageView: ImageView
    lateinit var title: TextView
    lateinit var tvCategory: TextView
    lateinit var tvRating: TextView
    lateinit var description: TextView
    lateinit var tvSize: TextView
    lateinit var btnCart: MaterialButton
    lateinit var btnFav: MaterialButton
    lateinit var productModelItem: ProductModelItem
    lateinit var sqlDb: ProduktSQLiteHjelper
    private lateinit var cartBtn: ImageView
    private lateinit var imgBack: ImageView
    private lateinit var historyBtn: ImageView
    private var isFave: Boolean = false

    // Setter opp aktiviteten, initialiserer UI-elementer og laster produktdata.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)
        window.statusBarColor = ContextCompat.getColor(this, R.color.main)
        imageView = findViewById(R.id.imgProduct)
        title = findViewById(R.id.tvTitle)
        tvCategory = findViewById(R.id.tvCategory)
        tvRating = findViewById(R.id.tvRating)
        description = findViewById(R.id.tvDescription)
        tvSize = findViewById(R.id.tvTotal)
        btnCart = findViewById(R.id.btnAdd);
        btnFav = findViewById(R.id.btnFav);
        sqlDb = ProduktSQLiteHjelper(this)
        cartBtn = findViewById(R.id.imgCart)
        imgBack = findViewById(R.id.imgBack)
        historyBtn = findViewById(R.id.imgHistory)
        isFave = intent.getBooleanExtra("isFav", false)

        if (isFave) {
            btnFav.setBackgroundColor(Color.RED)
            btnFav.setText("Fjern fra Favoritter")
        }
        productModelItem = intent.getSerializableExtra("data") as ProductModelItem

        // Setter opp og viser produktinformasjon som tittel, kategori, vurdering og beskrivelse
        productModelItem.let {
            title.text = productModelItem.title
            tvCategory.text = productModelItem.category
            tvSize.text = "(" + productModelItem.rating.count.toString() + ")"
            tvRating.text = productModelItem.rating.rate.toString()
            description.text = productModelItem.description
            Glide.with(this).load(it.image).into(imageView)
        }

        // Navigasjon tilbake til forrige skjerm, handlekurv og kjøpshistorikk.
        imgBack.setOnClickListener {
            finish()
        }
        // Klikkhåndterere for å legge til produktet i handlekurven eller favoritter.
        btnCart.setOnClickListener {
            val entity = ProduktEntitet(
                productModelItem.id.toLong(),
                productModelItem.title,
                productModelItem.price,
                productModelItem.description,
                productModelItem.category,
                productModelItem.image,
                productModelItem.rating.rate,
                productModelItem.rating.count, 1
            )
            val id = sqlDb.insertProduct(entity)
            if (id > 0) {
                Toast.makeText(this, "Lagt til handlekurv", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Produktet ligger allerede i handlekurven", Toast.LENGTH_SHORT).show()
            }

        }

        btnFav.setOnClickListener {
            if (!isFave) {
                val entity = ProduktEntitet(
                    productModelItem.id.toLong(),
                    productModelItem.title,
                    productModelItem.price,
                    productModelItem.description,
                    productModelItem.category,
                    productModelItem.image,
                    productModelItem.rating.rate,
                    productModelItem.rating.count, 1
                )
                val id = sqlDb.addToFav(entity)
                if (id) {
                    Toast.makeText(this, "Lagt til i favoritter", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Noe gikk galt", Toast.LENGTH_SHORT).show()
                }
            } else {
                val isRemoved = sqlDb.removefromFav(productModelItem.id.toLong())

                if (isRemoved) {
                    Toast.makeText(this, "Fjernet fra favoritter", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Noe gikk galt", Toast.LENGTH_SHORT).show()
                }
            }
        }
        historyBtn.setOnClickListener {
            startActivity(Intent(this@ProduktDetaljer, Historikk::class.java))
        }


        cartBtn.setOnClickListener {
            startActivity(Intent(this@ProduktDetaljer, Handlekurv::class.java))
        }
    }
}
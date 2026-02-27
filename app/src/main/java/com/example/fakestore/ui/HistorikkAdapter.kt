package com.example.fakestore.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fakestore.R
import com.example.fakestore.sql.HistorikkEntitet
import com.example.fakestore.sql.ProduktSQLiteHjelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistorikkAdapter(
    var context: Context,
    var list: List<HistorikkEntitet>,
    var sqLiteHelper: ProduktSQLiteHjelper
) :
    // ViewHolder som holder visningslogikk for hvert element i historikklisten.
    RecyclerView.Adapter<HistorikkAdapter.Vh>() {
    inner class Vh(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDateTime = itemView.findViewById<TextView>(R.id.tvDateTime)
        val bodyTv = itemView.findViewById<TextView>(R.id.tvItems)
        val tvPrice = itemView.findViewById<TextView>(R.id.tvPrice)
    }
    // Lager og returnerer ViewHolder for hvert listeelement
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        val view = LayoutInflater.from(context).inflate(R.layout.list_history, parent, false)
        return Vh(view)
    }

    // Returnerer størrelsen på historikklisten.
    override fun getItemCount(): Int = list.size
    @SuppressLint("SetTextI18n", "SimpleDateFormat")


    // Binder data til ViewHolder, viser detaljer som dato, pris og produktene i kjøpet.
    override fun onBindViewHolder(holder: Vh, position: Int) {
        val item = list[position]
        val cleanIdsString = item.productsIds?.substring(1, item.productsIds!!.length - 1)
        val idStrings = cleanIdsString?.split(", ")

        val idList = idStrings?.map { it.toLong() }
        val builer = StringBuilder()
        idList?.forEachIndexed { i, it ->
            val product = sqLiteHelper.getProductById(it)
            builer.append("${i + 1}. ")
            builer.append(product?.title.toString() + "\n")
        }

        val builder = StringBuilder()

        builder.append("Some example text")


        holder.tvDateTime.text = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale("no", "NO"))
            .format(Date(item.created ?: System.currentTimeMillis()))

        holder.bodyTv.text = builder.toString()

        holder.tvPrice.text = item.price + " ( ${idList?.size ?: 0} items)"

        holder.itemView.setOnClickListener {
            val intent=Intent(context,HistorikkDetaljer::class.java)
            intent.putExtra("id",item.id)
            intent.putExtra("id_product",item.productsIds)
            context.startActivity(intent)
        }

    }
}
package com.example.fakestore.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.fakestore.R
import com.example.fakestore.model.ProductModelItem


class ProduktAdapter(
    private val context: Context, var isFav: Boolean, var productList: MutableList<ProductModelItem>
) : RecyclerView.Adapter<ProduktAdapter.ProductViewHolder>() {

    // Oppretter og returnerer en ViewHolder for hvert produkt.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_products, parent, false)
        return ProductViewHolder(view)
    }

    // Oppdaterer adapterens produktliste og varsler om endringen.
    fun submitData(list: MutableList<ProductModelItem>) {
        productList = list
        notifyDataSetChanged()
    }

    //  Binder produktdata til den gitte ViewHolder.
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]


        holder.bind(product)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProduktDetaljer::class.java)
            intent.putExtra("data", product)
            intent.putExtra("isFav", isFav)
            context.startActivity(intent)
        }
    }

    // Returnerer antall produkter i listen.
    override fun getItemCount(): Int {
        return productList.size
    }

    // Inneklasse for å holde og binde data til visningselementer for hvert produkt.
    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tvTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.tvCategory)
        private val priceTextView: TextView = itemView.findViewById(R.id.tvPrice)
        private val imageView: ImageView = itemView.findViewById(R.id.imgCards)

        fun bind(product: ProductModelItem) {
            titleTextView.text = product.title
            descriptionTextView.text = product.category
            priceTextView.text = product.price.toString()
            Glide.with(context).asBitmap().load(Uri.parse(product.image))
                .into(object : CustomTarget<Bitmap?>() {
                    override fun onResourceReady(
                        resource: Bitmap, transition: Transition<in Bitmap?>?
                    ) {
                        imageView.setImageBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
        }
    }


}

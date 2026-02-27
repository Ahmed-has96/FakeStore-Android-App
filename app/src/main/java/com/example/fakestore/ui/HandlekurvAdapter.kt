package com.example.fakestore.ui

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fakestore.R
import com.example.fakestore.sql.ProduktEntitet

class HandlekurvAdapter(private val context: Context, private val productList: List<ProduktEntitet>, var delete:(ProduktEntitet)->Unit, var onBillChange:()->Unit) :
    RecyclerView.Adapter<HandlekurvAdapter.ProductViewHolder>() {

    // Oppretter nye visuelle elementer for hvert produkt i listen.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_cart, parent, false)
        return ProductViewHolder(view)
    }
    // Setter opp hvert listeelement med data fra produktlisten.
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]


        holder.bind(product)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProduktDetaljer::class.java)
            //intent.putExtra("data", product)
            //context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }
    // Inneholder logikken for hvordan hvert produkt skal vises i listen.
    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tvTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.tvCategory)
        private val priceTextView: TextView = itemView.findViewById(R.id.tvPrice)
        private val quantity: TextView = itemView.findViewById(R.id.textView2)
        private val imageView: ImageView = itemView.findViewById(R.id.imgCards)
        private val imgDelete: ImageView = itemView.findViewById(R.id.btnDelete)
        private val btnAdd: ImageButton = itemView.findViewById(R.id.btnAdd)
        private val btnRemove: ImageButton = itemView.findViewById(R.id.btnRemove)

        fun bind(product: ProduktEntitet) {
            titleTextView.text = product.title
            descriptionTextView.text = product.category
            priceTextView.text = product.price.toString()
            Glide.with(itemView).load(product.image).into(imageView)
            quantity.text = product.quantity.toString()

            btnAdd.setOnClickListener {
                product.quantity=product.quantity+1
                quantity.text=product.quantity.toString();
                onBillChange()
            }

            btnRemove.setOnClickListener {
                if (product.quantity != 0) {
                    product.quantity = product.quantity - 1
                    quantity.text = product.quantity.toString();
                    onBillChange()
                }
            }
            imgDelete.setOnClickListener {
                delete(product)
            }

        }
    }
}

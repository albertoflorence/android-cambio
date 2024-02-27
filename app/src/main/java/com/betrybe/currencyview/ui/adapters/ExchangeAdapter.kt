package com.betrybe.currencyview.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.betrybe.currencyview.R

class ExchangeAdapter (private val exchangeList: List<ExchangeData>) :
    Adapter<ExchangeAdapter.ExchangeViewHolder>() {

    class ExchangeViewHolder(view: View) : ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.item_exchange_title)
        val rate: TextView = view.findViewById(R.id.item_exchange_rate)
    }

    override fun getItemCount(): Int = exchangeList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exchange_layout, parent, false)
        return ExchangeViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExchangeViewHolder, position: Int) {
        holder.title.text = exchangeList[position].title
        holder.rate.text = exchangeList[position].rate.toString()
    }

}
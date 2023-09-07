package com.pg.gajamap.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pg.gajamap.R
import com.pg.gajamap.data.response.SearchResultData

class SearchResultAdapter(val itemList: ArrayList<SearchResultData>): RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {
    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_result, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = itemList[position].name

        // 아이템 클릭 이벤트
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position, itemList[position].index)
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tv_search_name)
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int, index: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener
}
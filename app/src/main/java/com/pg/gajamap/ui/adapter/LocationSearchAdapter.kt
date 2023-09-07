package com.pg.gajamap.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pg.gajamap.R
import com.pg.gajamap.data.response.LocationSearchData


class LocationSearchAdapter(val itemList: ArrayList<LocationSearchData>): RecyclerView.Adapter<LocationSearchAdapter.ViewHolder>() {
    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_location_search, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = itemList[position].name
        holder.road.text = itemList[position].address

        // 아이템의 배경 설정
        if(position == selectedPosition){
            holder.itemView.setBackgroundResource(R.color.inform)
            holder.itemView.findViewById<Button>(R.id.btn_plus).visibility = View.VISIBLE
        }else{
            holder.itemView.setBackgroundResource(R.color.white)
            holder.itemView.findViewById<Button>(R.id.btn_plus).visibility = View.GONE
        }
        // 아이템 클릭 이벤트
        holder.itemView.setOnClickListener {
            // 이전에 선택된 아이템의 배경을 변경
            val previousSelectedPosition = selectedPosition
            selectedPosition = holder.position
            notifyItemChanged(previousSelectedPosition)
            // 현재 클릭된 아이템의 배경을 변경
            notifyItemChanged(selectedPosition)

            itemClickListener.onClick(it, position)
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tv_name)
        val road: TextView = itemView.findViewById(R.id.tv_address)
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener
}
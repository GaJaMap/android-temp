package com.pg.gajamap.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pg.gajamap.databinding.ItemPhoneBinding
import com.pg.gajamap.ui.fragment.setting.ContactsData

class PhoneListAdapter(private val dataList : ArrayList<ContactsData>): RecyclerView.Adapter<PhoneListAdapter.ViewHolder>() {

    private val checkedPositions = mutableSetOf<Int>()
    inner class ViewHolder(private val binding: ItemPhoneBinding):
            RecyclerView.ViewHolder(binding.root){
                fun bind(data : ContactsData){
                    binding.itemPhoneTv.text = data.name
                    binding.itemPhoneTv.isChecked = checkedPositions.contains(absoluteAdapterPosition)
                    binding.itemPhoneTv.setOnClickListener {
                        if(binding.itemPhoneTv.isChecked){
                            checkedPositions.add(absoluteAdapterPosition)
                        }else {
                            checkedPositions.remove(absoluteAdapterPosition)
                        }
                    }
                }
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPhoneBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])

        holder.itemView.setOnClickListener{
            itemClickListener.onClick(it, position)
        }
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener

    fun setAllItemsChecked(checked: Boolean) {
        if (checked) {
            checkedPositions.addAll(dataList.indices)
        } else {
            checkedPositions.clear()
        }
        notifyDataSetChanged()
    }

    fun isChecked(position: Int): Boolean {
        return checkedPositions.contains(position)
    }
}
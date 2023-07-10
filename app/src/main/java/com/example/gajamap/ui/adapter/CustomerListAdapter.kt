package com.example.gajamap.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gajamap.databinding.ItemListBinding
import com.example.gajamap.ui.fragment.customerList.Customer

class CustomerListAdapter(private val dataList : ArrayList<Customer>): RecyclerView.Adapter<CustomerListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemListBinding):
            RecyclerView.ViewHolder(binding.root){
                fun bind(data: Customer){
                    Glide.with(binding.itemProfileImg.context)
                        .load(data.img)
                        .into(binding.itemProfileImg)
                    binding.itemProfileAddressDetail.text = data.address
                    binding.itemProfileName.text = data.name
                    binding.itemProfilePhoneDetail.text = data.phone
                    binding.itemProfileDistance.text = data.distance

                }
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
}
package com.pg.gajamap.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pg.gajamap.R
import com.pg.gajamap.data.model.ViewPagerData
import com.pg.gajamap.databinding.ItemViewpagerBinding

class ViewPagerAdapter (val itemList: ArrayList<ViewPagerData>): RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : PagerViewHolder{
        val binding=ItemViewpagerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PagerViewHolder(binding)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int){
        holder.bind(itemList[position])
    }

    inner class PagerViewHolder(private val binding: ItemViewpagerBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ViewPagerData){
            if(item.profileImg == "null"){
                Glide.with(itemView).load(R.drawable.profile_img_origin).into(binding.ivCardProfile)
            }
            else{
                Glide.with(itemView).load(item.profileImg).into(binding.ivCardProfile)
            }
            binding.tvCardName.text = item.name
            binding.tvCardAddressDetail.text = item.address
            binding.tvCardPhoneDetail.text = item.phoneNumber
            if(item.distance == null) {
                binding.tvCardDistance.text = "-"
            }
            else {
                binding.tvCardDistance.text = String.format("%.2f", item.distance?.times(0.001))
            }
        }
    }
}
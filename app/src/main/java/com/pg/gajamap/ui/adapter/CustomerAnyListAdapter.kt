package com.pg.gajamap.ui.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.pg.gajamap.R
import com.pg.gajamap.base.GajaMapApplication
import com.pg.gajamap.data.model.Client
import com.pg.gajamap.databinding.ItemAnyListBinding

class CustomerAnyListAdapter(private val dataList: List<Client>): RecyclerView.Adapter<CustomerAnyListAdapter.ViewHolder>() {


    private var selectPos :Boolean = false
    private var pos : Int = -1

    // 원래 배경 리소스 ID (클릭 전의 배경)
    private var originalBackgroundResource: Int = 0

    private var itemBackground: Drawable? = null

    inner class ViewHolder(private val binding: ItemAnyListBinding):
            RecyclerView.ViewHolder(binding.root){
        fun bind(data: Client, background: Drawable?){
            val address = data.address.mainAddress
            val filePath = data.image.filePath
            val imageUrl = GajaMapApplication.prefs.getString("imageUrlPrefix", "")
            val file = imageUrl + data.image.filePath
            Glide.with(binding.itemProfileImg.context)
                .load(file)
                .fitCenter()
                .apply(RequestOptions().override(500,500))
                .error(R.drawable.profile_img_origin)
                .into(binding.itemProfileImg)
            /*if(filePath != null){
                Glide.with(binding.itemProfileImg.context)
                    .load(file)
                    .fitCenter()
                    .apply(RequestOptions().override(500,500))
                    .error(R.drawable.profile_img_origin)
                    .into(binding.itemProfileImg)
            }
            if(imageUrl == null){
                Glide.with(binding.itemProfileImg.context)
                    .load(file)
                    .fitCenter()
                    .apply(RequestOptions().override(500,500))
                    .error(R.drawable.profile_img_origin)
                    .into(binding.itemProfileImg)
            }
            else{
                Glide.with(binding.itemProfileImg.context)
                    .load(R.drawable.profile_img_origin)
                    .fitCenter()
                    .apply(RequestOptions().override(500,500))
                    .error(R.drawable.profile_img_origin)
                    .into(binding.itemProfileImg)
            }*/
            binding.itemProfileAddressDetail.text = address
            binding.itemProfileName.text = data.clientName
            binding.itemProfilePhoneDetail.text = data.phoneNumber
            itemView.background = background

        }
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAnyListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(dataList[position], itemBackground)

        if(!selectPos){
            holder.itemView.setOnClickListener{
                itemClickListener.onClick(it, position)
                holder.itemView.setBackgroundResource(R.drawable.fragment_list_tool_purple)
                selectPos = true
            }
        }

    }

    fun updateItemBackground(background: Drawable?) {
        itemBackground = background
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener

}
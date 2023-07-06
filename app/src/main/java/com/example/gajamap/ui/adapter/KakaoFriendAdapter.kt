package com.example.gajamap.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gajamap.databinding.ItemPhoneBinding

class KakaoFriendAdapter(private val dataList: List<com.kakao.sdk.talk.model.Friend>): RecyclerView.Adapter<KakaoFriendAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding : ItemPhoneBinding ):
            RecyclerView.ViewHolder(binding.root){
                fun bind(data: com.kakao.sdk.talk.model.Friend){
                    binding.itemPhoneTv.text = data.profileNickname
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

        /*holder.itemView.setOnClickListener{
            itemClickListener.onClick(it, position)
        }*/
    }

    /*interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener*/

}
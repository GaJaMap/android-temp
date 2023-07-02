package com.example.gajamap.ui.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gajamap.api.data.remote.GroupListData
import com.example.gajamap.databinding.ItemGroupListBinding

class GroupListAdapter: RecyclerView.Adapter<GroupListAdapter.ViewHolder>() {
    var datalist = mutableListOf<GroupListData>()

    inner class ViewHolder(private val binding: ItemGroupListBinding): RecyclerView.ViewHolder(binding.root) {
        val bgShape = binding.ivGroup.background as GradientDrawable
        fun bind(item: GroupListData){
            bgShape.setColor(item.img)
            binding.tvGroup.text = item.name
            binding.tvGroupperson.text = item.person.toString()
        }
    }

    // 만들어진 뷰홀더 없을때 뷰홀더 (레이아웃) 생성하는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding=ItemGroupListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = datalist.size

    // recyclerview가 viewholder를 가져와 데이터 연결 할 때 호출
    // 적절한 데이터를 가져와서 그 데이터를 사용하여 뷰홀더의 레이아웃 채움
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datalist[position])
    }
}

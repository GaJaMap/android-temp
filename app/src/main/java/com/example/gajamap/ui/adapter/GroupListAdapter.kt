package com.example.gajamap.ui.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.persistableBundleOf
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gajamap.data.model.GroupListData
import com.example.gajamap.databinding.ItemGroupListBinding

class GroupListAdapter(private val groupDeleteListener: GroupDeleteListener, private val groupEditListener: GroupEditListener): RecyclerView.Adapter<GroupListAdapter.ViewHolder>() {
    var datalist = mutableListOf<GroupListData>()

    inner class ViewHolder(private val binding: ItemGroupListBinding): RecyclerView.ViewHolder(binding.root) {
        val bgShape = binding.ivGroup.background as GradientDrawable
        fun bind(item: GroupListData){
            bgShape.setColor(item.img)
            binding.item = item
            // 삭제, 수정 버튼 눌렀을 때의 이벤트
            binding.ivDelete.setOnClickListener {
                groupDeleteListener.click(datalist[position].id, datalist[position].name, position)
            }
            binding.ivModify.setOnClickListener {
                groupEditListener.click2(datalist[position].id, datalist[position].name, position)
            }
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
        // 아이템 클릭 이벤트
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position, datalist[position].id, datalist[position].name)
        }
    }
    // 아이템 삭제 버튼 클릭
    interface GroupDeleteListener{
        fun click(id: Long, name: String, position: Int)
    }
    // 아이템 수정 버튼 클릭
    interface GroupEditListener{
        fun click2(id: Long, name: String, position: Int)
    }
    // 아이템 클릭
    interface OnItemClickListener {
        fun onClick(v: View, position: Int, gid: Long, gname: String)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener

    fun setData(data : ArrayList<GroupListData>){
        datalist = data
        notifyDataSetChanged()
    }
}

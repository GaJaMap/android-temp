package com.pg.gajamap.ui.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.pg.gajamap.R
import com.pg.gajamap.base.UserData
import com.pg.gajamap.data.model.GroupListData
import com.pg.gajamap.databinding.ItemGroupListBinding

class GroupListAdapter(private val groupDeleteListener: GroupDeleteListener, private val groupEditListener: GroupEditListener): RecyclerView.Adapter<GroupListAdapter.ViewHolder>() {
    var datalist = mutableListOf<GroupListData>()
    private var selectedPosition : Int = 0
    private var previousSelectedPosition : Int = 0

    inner class ViewHolder(private val binding: ItemGroupListBinding): RecyclerView.ViewHolder(binding.root) {
        val bgShape = binding.ivGroup.background as GradientDrawable

        fun bind(item: GroupListData){
            bgShape.setColor(item.img)
            binding.item = item
            for (i in datalist.indices) {
                val data = datalist[i]
                if (data.id == UserData.groupinfo!!.groupId) {
                    selectedPosition = i
                    break
                }
            }
            datalist[selectedPosition].isSelected = true

            // 삭제, 수정 버튼 눌렀을 때의 이벤트
            binding.ivDelete.setOnClickListener {
                groupDeleteListener.click(datalist[position].id, datalist[position].name, position)
                selectedPosition = 0
                datalist[0].isSelected = true
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
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.bind(datalist[position])

        Log.d("pos", position.toString())

        if(datalist[position].whole && position == 0) {
            holder.itemView.findViewById<ImageView>(R.id.iv_modify).visibility = View.GONE
            holder.itemView.findViewById<ImageView>(R.id.iv_delete).visibility = View.GONE
        }

        // 아이템의 배경 설정
        if(datalist[position].isSelected && selectedPosition == position){
            holder.itemView.setBackgroundResource(R.color.inform)

        }else{
            holder.itemView.setBackgroundResource(R.color.white)
        }

        if(!datalist[position].whole && selectedPosition != 0 && position != 0){
            holder.itemView.findViewById<ImageView>(R.id.iv_modify).visibility = View.VISIBLE
            holder.itemView.findViewById<ImageView>(R.id.iv_delete).visibility = View.VISIBLE
        }
        // 아이템 클릭 이벤트
        holder.itemView.setOnClickListener {
            // 이전에 선택된 아이템의 배경을 변경
            previousSelectedPosition = selectedPosition
            datalist[previousSelectedPosition].isSelected = false
            selectedPosition = position
            datalist[selectedPosition].isSelected = true
            // 이전 & 현재 클릭된 아이템의 배경을 변경
            notifyItemChanged(selectedPosition)
            notifyItemChanged(previousSelectedPosition)

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

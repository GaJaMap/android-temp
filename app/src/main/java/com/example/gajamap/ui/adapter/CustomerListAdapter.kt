package com.example.gajamap.ui.adapter

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gajamap.R
import com.example.gajamap.base.GajaMapApplication
import com.example.gajamap.data.model.Client
import com.example.gajamap.databinding.ItemListBinding
import de.hdodenhof.circleimageview.CircleImageView

class CustomerListAdapter(private var dataList: List<Client>): RecyclerView.Adapter<CustomerListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemListBinding):
            RecyclerView.ViewHolder(binding.root){
        val button1 : CircleImageView = binding.itemProfileImg
        val button2 : TextView = binding.itemProfileName
        val button3 : TextView = binding.itemProfileAddressDetail
        val button4 : TextView = binding.itemProfileAddress
        val button5 : TextView = binding.itemProfilePhoneDetail
        val button6 : TextView = binding.itemProfilePhone
        val buttonNavi : ConstraintLayout = binding.itemProfileCarBtn
        init {
            binding.itemProfilePhoneBtn.setOnClickListener {
                Log.d("phone", "why")
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val client = dataList[position]
                    val phoneNumber = client.phoneNumber

                    // 여기서 전화 걸기 기능을 수행하도록 코드를 작성합니다.
                    // 예를 들어, 다음과 같이 작성할 수 있습니다:
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:$phoneNumber")
                    binding.root.context.startActivity(intent)
                }
            }
        }

        fun bind(data: Client){
                    val address = data.address.mainAddress
            if(data.distance.toString() == "null"){
                val distance1 = "- " + "km"
                binding.itemProfileDistance.text = distance1
            }else {
                val distance = data.distance.toString()
                Log.d("distance", distance.toString())
                val distance1 = distance + "km"
                binding.itemProfileDistance.text = distance1
            }
                    val filePath = data.image.filePath
                    val imageUrl = GajaMapApplication.prefs.getString("imageUrlPrefix", "")
                    val file = imageUrl + data.image.filePath
                    Log.d("img_file", file.toString())
                    Glide.with(binding.itemProfileImg.context)
                        .load(file)
                        .fitCenter()
                        .apply(RequestOptions().override(500,500))
                        .error(R.drawable.profile_img_origin)
                        .into(binding.itemProfileImg)
                    binding.itemProfileAddressDetail.text = address
                    binding.itemProfileName.text = data.clientName
                    binding.itemProfilePhoneDetail.text = data.phoneNumber


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

        holder.button1.setOnClickListener{
            itemClickListener.onClick(it, position)
        }
        holder.button2.setOnClickListener{
            itemClickListener.onClick(it, position)
        }
        holder.button3.setOnClickListener{
            itemClickListener.onClick(it, position)
        }
        holder.button4.setOnClickListener{
            itemClickListener.onClick(it, position)
        }
        holder.button5.setOnClickListener{
            itemClickListener.onClick(it, position)
        }
        holder.button6.setOnClickListener{
            itemClickListener.onClick(it, position)
        }
        holder.buttonNavi.setOnClickListener {
            naviClickListener.onClick(it, position)
        }
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    interface ItemClickListener{
        fun onClick(v: View, position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }

    fun setItemClickListener(itemClickListener : ItemClickListener){
        this.naviClickListener = itemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener
    private lateinit var naviClickListener : ItemClickListener

    fun updateData(newDataList: List<Client>) {
        dataList = newDataList
        notifyDataSetChanged()
    }
}

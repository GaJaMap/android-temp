package com.example.gajamap.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gajamap.R
import com.example.gajamap.data.model.Client
import com.example.gajamap.databinding.ItemListBinding

class CustomerListAdapter(private var dataList: List<Client>): RecyclerView.Adapter<CustomerListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemListBinding):
            RecyclerView.ViewHolder(binding.root){
                fun bind(data: Client){
                    val address = data.address.mainAddress
                    val distance = data.distance.toString()
                    val distance1 = distance + "km"
                    val filePath = data.image.filePath
                    val file = data.imageUrlPrefix + data.image.filePath
                    Log.d("img_file", file.toString())
                    if(filePath != null){
                        Glide.with(binding.itemProfileImg.context)
                            .load(file)
                            .fitCenter()
                            .apply(RequestOptions().override(500,500))
                            .error(R.drawable.profile_img_origin)
                            .into(binding.itemProfileImg)
                    }
                    if(data.imageUrlPrefix == null){
                        Glide.with(binding.itemProfileImg.context)
                            .load(filePath)
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
                    }
                    binding.itemProfileAddressDetail.text = address
                    binding.itemProfileName.text = data.clientName
                    binding.itemProfilePhoneDetail.text = data.phoneNumber
                    binding.itemProfileDistance.text = distance1

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


    fun getImageUrl(imageName: String): String {
        Log.d("img", imageName)
        /*val filePath = "/path/to/images/$imageName"
        val url = URL("file://$filePath")*/
        val url = "content://media/external/images/$imageName"
        //val url = URL("content://media/external/images/$imageName")

        return url.toString()

    }

    fun updateData(newDataList: List<Client>) {
        dataList = newDataList
        notifyDataSetChanged()
    }
}

package com.sunshinesky.android.albbWidget.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.sunshinesky.android.albbModel.AlbbAppPackageModel

import com.sunshinesky.android.R

class AlbbHomeAppAdapter (mListData: ArrayList<AlbbAppPackageModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mList: ArrayList<AlbbAppPackageModel> = mListData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        var view = LayoutInflater.from(parent.context).inflate(R.layout.albb_home_app_item, parent, false)

        return HomeAppHolder(view)
    }

    public fun  setDataList(list: ArrayList<AlbbAppPackageModel>) {
        mList =  list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mList?.size
    }

    override fun getItemViewType(position: Int): Int {
        if (position < mList.size) {
            val obj: AlbbAppPackageModel = mList.get(position)
            return obj.itemType;
        }
        return super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var model = mList[position]
        if (holder is HomeAppHolder) {
            if (model.icon == null) {
                holder.iv_icon.setImageResource(R.mipmap.ic_launcher_round)
            } else {
               holder.iv_icon.setImageDrawable(model.icon)
            }
        }
    }

    inner class HomeAppHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_icon: ImageView = itemView.findViewById(R.id.iv_app_item)
    }


}
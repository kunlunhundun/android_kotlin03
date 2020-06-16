/*
 * Copyright © 2020 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.wireguard.android.alWidget.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wireguard.android.R
import com.wireguard.android.alModel.AppPackageModel

class  AppFilterAdapter(mList: ArrayList<AppPackageModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_TITLE = 1
    private val ITEM_CONTENT = 2

    var mList: ArrayList<AppPackageModel> = mList
    var curPosition: Int = -1
    var onItemClickLister: AppFilterAdapter.OnItemClickLister? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0 || viewType == 2) {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.ali_app_select_title_item, parent, false)
            return  AppFilterTitleHolder(v)
        }

        var view = LayoutInflater.from(parent.context).inflate(R.layout.ali_app_select_item, parent, false)

        return AppFilterHolder(view)
    }

    public fun  setDataList(list: ArrayList<AppPackageModel>) {
        mList =  list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mList?.size
    }

    override fun getItemViewType(position: Int): Int {
        if (position < mList.size) {
            val obj: AppPackageModel = mList.get(position)
            return obj.itemType;
        }
        return super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var model = mList[position]
        if (holder is AppFilterTitleHolder) {
            if (model.itemType == 0) {
                holder.title.text = "exlude app vpn"
                return
            }
            holder.title.text = "inclue app vpn"
        }
        if (holder is AppFilterHolder) {
            holder.cbAppSelect.setOnClickListener {
                curPosition = position
                onItemClickLister?.onItemClick(position)
            }
            if ( model.itemType == 1) {
                holder.cbAppSelect.isChecked = false
            } else {
                holder.cbAppSelect.isChecked = true
            }
            holder.appName.text = model.name
            holder.ivAppIcon.setImageDrawable(model.icon)
        }
    }

    inner class AppFilterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivAppIcon: ImageView = itemView.findViewById(R.id.iv_icon_app)
        var cbAppSelect: CheckBox = itemView.findViewById(R.id.cb_app_select)
        var appName : TextView = itemView.findViewById(R.id.tv_app_name)
    }
    inner class AppFilterTitleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.tv_item_title)
    }

    interface OnItemClickLister {
        fun onItemClick(position: Int)
    }

}
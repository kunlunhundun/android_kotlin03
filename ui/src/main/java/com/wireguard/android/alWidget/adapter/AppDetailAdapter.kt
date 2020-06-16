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
import com.wireguard.android.alModel.AppPackageModel
import com.wireguard.android.R

class AppDetailAdapter (mList: MutableList<AppPackageModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mList: MutableList<AppPackageModel> = mList
    var onItemClickLister: OnItemClickLister? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        var view = LayoutInflater.from(parent.context).inflate(R.layout.ali_app_check_item, parent, false)
        return AppDetailHolder(view)
    }

    override fun getItemCount(): Int {
        return mList?.size
    }

    override fun getItemViewType(position: Int): Int {

        return super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var model = mList[position]

        if (holder is AppDetailHolder) {
            holder.mItemView.setOnClickListener {
                model.isCheck = !model.isCheck
                holder.checkBox.isChecked = model.isCheck
                onItemClickLister?.onItemClick(position)
            }
            holder.appName.text = model.name
            holder.appIcon.setImageDrawable(model.icon)
            holder.checkBox.isChecked = model.isCheck
        }
    }

    inner class AppDetailHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var appIcon: ImageView = itemView.findViewById(R.id.iv_icon_app)
        var appName : TextView = itemView.findViewById(R.id.tv_app_name)
        var checkBox: CheckBox = itemView.findViewById(R.id.cb_app_detail)
        var mItemView:View = itemView
    }
    interface OnItemClickLister {
        fun onItemClick(position: Int)
    }

}
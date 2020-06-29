package com.wireguard.android.alWidget.adapter

import android.app.Application
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.wireguard.android.R
import com.wireguard.android.aliData.AppConfigData
import com.wireguard.android.aliData.response.MessageInfoResonse.MessageObj


class MessageAdapter (mListDada: ArrayList<MessageObj>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mList = mListDada
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.ali_message_item, parent, false)

        return MessageHolder(view)
    }

    public fun  setDataList(list: ArrayList<MessageObj>) {
        mList =  list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var model = mList[position]
        if (holder is MessageHolder) {
            holder.tv_question.text = model.comment?.content
            holder.tv_username.text = AppConfigData.loginName
            holder.tv_answer.text = ""
            if (model.officialReplyList?.size ?: 0 > 0) {
                holder.tv_answer.text = model.officialReplyList?.get(0)?.content
                holder.cl_answer.visibility = View.VISIBLE
            } else {
                holder.cl_answer.visibility = View.GONE
            }
        }
    }

    inner class MessageHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tv_username: TextView = itemView.findViewById(R.id.tv_username)
        var tv_time : TextView = itemView.findViewById(R.id.tv_time)
        var tv_question : TextView = itemView.findViewById(R.id.tv_question)
        var tv_answer : TextView = itemView.findViewById(R.id.tv_answer)
        var cl_answer: ConstraintLayout = itemView.findViewById(R.id.cl_answer)
    }
}
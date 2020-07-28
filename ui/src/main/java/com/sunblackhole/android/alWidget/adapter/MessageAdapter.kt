package com.sunblackhole.android.alWidget.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.sunblackhole.android.R
import com.sunblackhole.android.aliData.AppConfigData
import com.sunblackhole.android.aliData.response.MessageInfoResonse.MessageObj


class MessageAdapter (mListDada: ArrayList<MessageObj>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mList = mListDada
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.ali_message_item, parent, false)

        return MessageHolder(view)
    }

     fun  setDataList(list: ArrayList<MessageObj>) {
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
            holder.tv_answer.text = "We have received your message. We will replay you as soon as possible. Thank you!"
            holder.cl_answer.visibility = View.VISIBLE
            if (model.comment?.createTime != null) {
                var creatTime = model.comment?.createTime
                var newTime =  creatTime.toString().replace("T"," ").replace(".000+0000","")
                holder.tv_time.text = newTime
            }
            if (model.officialReplyList?.size ?: 0 > 0) {
                var content = ""
                var size = model.officialReplyList?.size
                var  i = 0;
                model.officialReplyList?.forEach {
                    var enter = "\n"
                    i++
                    if (i==size) {
                        enter = ""
                    }
                   content = content +  it.content + enter
                }

                holder.tv_answer.text = content

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
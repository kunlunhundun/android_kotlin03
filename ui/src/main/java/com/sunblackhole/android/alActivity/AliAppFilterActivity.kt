/*
 * Copyright © 2020 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sunblackhole.android.alActivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.sunblackhole.android.Application
import com.sunblackhole.android.Application.Companion.CACHE_ALLOW_APP_FLAG
import com.sunblackhole.android.Application.Companion.CACHE_EXCCLUDE_DATA
import com.sunblackhole.android.Application.Companion.CACHE_INCLUDE_DATA
import com.sunblackhole.android.R
import com.sunblackhole.android.alModel.AppPackageModel
import com.sunblackhole.android.alModel.ReconnectTunnelEvent
import com.sunblackhole.android.alWidget.adapter.AppFilterAdapter
import com.sunblackhole.android.alutils.LogUtils
import kotlinx.android.synthetic.main.ali_app_filter_activity.*
import org.greenrobot.eventbus.EventBus

class AliAppFilterActivity : AliBaseActivity() {

    var mListData: ArrayList<AppPackageModel> = ArrayList()
    var includeAppList:ArrayList<AppPackageModel> = ArrayList();
    var excludeAppList:ArrayList<AppPackageModel> = ArrayList();
    var appFilterAdpater:AppFilterAdapter? = null
    var originalAppFlag: Int = 0
    var hasModifyData: Boolean = false


    companion object {
        val APP_ITEM_IS_INCLUDE = "APP_ITEM_IS_INCLUDE"
        var FILTER_RESULT_OK = 300

    }
    enum class AppItemType(val value: Int) {
        ALL(1),
        EXCLUDE(2),
        INCLUDE(3),
    }
//    enum class AppItemType{
//        ALL,
//        EXCLUDE,
//        INCLUDE
//    }
    var selectAppType: AppItemType = AppItemType.ALL


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initView()
        initListener()
    }

    override fun getLayoutId(): Int {
        return R.layout.ali_app_filter_activity
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    fun initData(){

        setTile("VPN settings")

        var lastSelect = Application.getAcache().getAsString(CACHE_ALLOW_APP_FLAG) ?: "1"
        LogUtils.e("lastselect:",lastSelect)
        originalAppFlag = lastSelect.toInt()

        if (lastSelect.toInt() == 2) {
            selectAppType = AppItemType.EXCLUDE
        } else if (lastSelect.toInt() == 3) {
            selectAppType = AppItemType.INCLUDE
        } else {
            selectAppType = AppItemType.ALL
        }
        /* val appModel = AppPackageModel()
         appModel.itemType = 0
         appModel.name = "不使用vpn的app"
         mListData.add(appModel)
         val useAppModel = AppPackageModel()
         useAppModel.itemType = 2
         useAppModel.name = "使用vpn的app"
         mListData.add(useAppModel)
         mListData.addAll(2,Application.getIncludeAppList())
        // Application.getAcache().put("excludeApp", Gson().toJson(excludeAppList))
        // Application.getAcache().getAsString(CACHE_EXCCLUDE_DATA)
         val useAppModel1 = AppPackageModel() */
    }

    fun loadData() {

        if (selectAppType == AppItemType.ALL) {
            mListData = ArrayList();
        } else if (selectAppType == AppItemType.EXCLUDE) {
            mListData = Application.getExcludeAppList()
        } else if (selectAppType == AppItemType.INCLUDE) {
            mListData = Application.getIncludeAppList()
        }
        appFilterAdpater?.setDataList(mListData)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            backEvent()
            false
        } else {
            super.onKeyDown(keyCode, event);
        }
    }

    private fun initListener() {

         setBackListener(View.OnClickListener {

             backEvent()
         })
        cl_add_app.setOnClickListener {

            if (selectAppType == AppItemType.ALL){
                return@setOnClickListener
            }
            var intent = Intent(this,AliSelectAppDetailActivity::class.java)
            val isInclude: Boolean = if (selectAppType == AppItemType.INCLUDE) true else false
            intent.putExtra(APP_ITEM_IS_INCLUDE, isInclude)
            startActivity(intent)
        }

        cl_all_app.setOnClickListener {
            mListData = ArrayList()
            selectAppType = AppItemType.ALL
            Application.getAcache().put(CACHE_ALLOW_APP_FLAG,selectAppType.value.toString())

            rv_app_channel.visibility = View.GONE
            ck_all_app_choose.isChecked = true
            ck_exclude_app_choose.isChecked = false
            ck_include_app_choose.isChecked = false
        }
        cl_exclude_app.setOnClickListener {
            selectAppType = AppItemType.EXCLUDE
            mListData = Application.getExcludeAppList()
            appFilterAdpater?.setDataList(mListData)
            Application.getAcache().put(CACHE_ALLOW_APP_FLAG,  selectAppType.value.toString())

            rv_app_channel.visibility = View.VISIBLE
            ck_all_app_choose.isChecked = false
            ck_exclude_app_choose.isChecked = true
            ck_include_app_choose.isChecked = false
        }
        cl_include_app.setOnClickListener {
            selectAppType = AppItemType.INCLUDE
            mListData = Application.getIncludeAppList()
            appFilterAdpater?.setDataList(mListData)
            Application.getAcache().put(CACHE_ALLOW_APP_FLAG,  selectAppType.value.toString())

            rv_app_channel.visibility = View.VISIBLE
            ck_all_app_choose.isChecked = false
            ck_exclude_app_choose.isChecked = false
            ck_include_app_choose.isChecked = true
        }
    }


    private fun backEvent() {

        if (selectAppType == AppItemType.EXCLUDE) {
            var appNameList: ArrayList<String> = ArrayList()
            mListData.forEach {
                appNameList.add(it.name!!)
            }
            val nameListStr =  Gson().toJson(appNameList)
            val cacheName = Application.getAcache().getAsString(CACHE_EXCCLUDE_DATA)
            if (nameListStr != cacheName){
                Application.get().isNeedConnectByModifyAppFlag = true
            }
            Application.getAcache().put(CACHE_EXCCLUDE_DATA, nameListStr)
        } else if (selectAppType == AppItemType.INCLUDE) {

            var appNameList: ArrayList<String> = ArrayList()
            mListData.forEach {
                appNameList.add(it.name!!)
            }
            val nameListStr =  Gson().toJson(appNameList)
            val cacheName = Application.getAcache().getAsString(CACHE_INCLUDE_DATA)
            if (nameListStr != cacheName){
                Application.get().isNeedConnectByModifyAppFlag = true
            }
            Application.getAcache().put(CACHE_INCLUDE_DATA, nameListStr)
        }
        var lastSelect = Application.getAcache().getAsString(CACHE_ALLOW_APP_FLAG) ?: "1"

        if (originalAppFlag  == 1) {
            if (lastSelect.toInt() != 1) {
                Application.get().isNeedConnectByModifyAppFlag = true
                EventBus.getDefault().post(ReconnectTunnelEvent())
            }
        } else {
            if ( Application.get().isNeedConnectByModifyAppFlag == true) {
                EventBus.getDefault().post(ReconnectTunnelEvent())
            }
        }
        val resultIntent = Intent()
        val bundle = Bundle()
        bundle.putString("key_select_app", "result")
        resultIntent.putExtras(bundle)
        this.setResult(FILTER_RESULT_OK, resultIntent)
        finish()
    }

    fun initView() {

        if (selectAppType == AppItemType.EXCLUDE) {
            rv_app_channel.visibility = View.VISIBLE
            ck_all_app_choose.isChecked = false
            ck_exclude_app_choose.isChecked = true
        } else if (selectAppType == AppItemType.INCLUDE) {
            rv_app_channel.visibility = View.VISIBLE
            ck_all_app_choose.isChecked = false
            ck_include_app_choose.isChecked = true
        }

        val manager =  LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false)
        rv_app_channel.setLayoutManager(manager);
        val addapter:AppFilterAdapter =  AppFilterAdapter(mListData);
        rv_app_channel.setAdapter(addapter);
        appFilterAdpater = addapter
        addapter.onItemClickLister = object : AppFilterAdapter.OnItemClickLister {
            override fun onItemClick(position: Int) {
                refreshAdapter(position)
            }
        }
        addapter.notifyDataSetChanged()
    }


    fun refreshAdapter(position: Int) {

        mListData.removeAt(position);
        appFilterAdpater?.notifyDataSetChanged()

      /*  var excludeCount = Application.getExcludeAppList().size;
        var inclueCount = Application.getIncludeAppList().size
        var excludePosition = position - 1
        var includePosition = position - excludeCount - 1 - 1

        if (  excludeCount + 1  >= position + 1) {

        } else {
            if (Application.getIncludeAppList().size > includePosition) {
                val appModel:AppPackageModel = mListData.get(position)
                appModel.itemType = 1
                Application.getIncludeAppList().removeAt( includePosition)
                Application.getExcludeAppList().add(appModel)
                mListData.removeAt(position)
                mListData.add(excludeCount+1,appModel)

                appFilterAdpater?.mList = mListData;
                appFilterAdpater?.notifyDataSetChanged()
            }
        } */
    }



    /*  class AppTypeObject {

         var itemType = 1
         var name: String = ""
         var appItemIcon: String = ""
     }

    inner class ListViewAdapter(context: Context, var datas: MutableList<AppTypeObject>) : BaseAdapter() {
         var mContext: Context = context

         override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

             val itemType = getItemViewType(position)
             when (itemType) {
                 0 -> {
                     val v = LayoutInflater.from(mContext).inflate(R.layout.ali_app_select_item, parent, false)
                     return v
                 }
                 2 -> {
                     val v = LayoutInflater.from(mContext).inflate(R.layout.ali_app_select_title_item, parent, false)
                     val label = v.findViewById<TextView>(R.id.tv_allapp_title)
                     label.text = "选择的app"
                   //  label.setTextColor(Color.parseColor("#999999"))
                     return v
                 }
                 else -> {
                     var holder: AppIconViewHolder
                     var v: View
                     if (convertView == null) {
                         holder = AppIconViewHolder()
                         v = LayoutInflater.from(mContext).inflate(R.layout.ali_app_select_item, parent, false)
                         holder.iv_icon_app = v.findViewById(R.id.iv_icon_app)
                         holder.tv_appname = v.findViewById(R.id.tv_app_name)
                         holder.cb_app_select = v.findViewById(R.id.cb_app_select)
                         holder.line = v.findViewById(R.id.app_line)
                         v.tag = holder

                     } else {
                         v = convertView
                         holder = v.tag as AppIconViewHolder
                     }
                    // holder.iv_icon_app.set
                   //  holder.iv_icon_app.setImageResource(datas[position].appItemIcon)
                     holder.tv_appname.text = (datas[position].name)
                     if (datas[position].itemType == 1) {
                         holder.cb_app_select.isChecked = false
                     } else {
                         holder.cb_app_select.isChecked = true
                     }
                     return v
                 }
             }
         }

         override fun getItem(p0: Int): Any {
             return datas[p0]
         }

         override fun getItemId(p0: Int): Long {
             return p0.toLong()
         }

         override fun getCount(): Int {
             return datas.size
         }

         override fun getViewTypeCount(): Int {
             return 3
         }

         override fun getItemViewType(position: Int): Int {

             return datas[position].itemType

         }

         inner class TitleHolder {
             lateinit var tv_item_title: TextView
         }
         inner class AppIconViewHolder {
             lateinit var cb_app_select: CheckBox
             lateinit var iv_icon_app: ImageView
             lateinit var tv_appname: TextView
             lateinit var line: TextView
         }
     } */

}


package com.sunshinesky.android.albbActivity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunshinesky.android.Application
import com.sunshinesky.android.R
import com.sunshinesky.android.albbModel.AlbbAppPackageModel
import com.sunshinesky.android.albbWidget.adapter.AlbbAppDetailAdapter
import kotlinx.android.synthetic.main.albb_select_app_detail_activity.*

class AlbbSelectAppDetailActivity : AlbbBaseActivity() {

    var mListData: ArrayList<AlbbAppPackageModel> = ArrayList()
    lateinit var albbAppDetailAdapter: AlbbAppDetailAdapter

    var isFromInclude: Boolean = false
    lateinit var selectedAlbbAppList: ArrayList<AlbbAppPackageModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initView()
    }

    override fun getLayoutId(): Int {
        return R.layout.albb_select_app_detail_activity
    }

    private fun initData() {

        setTile("App select")

        isFromInclude =  intent.getBooleanExtra(AlbbAppFilterActivity.APP_ITEM_IS_INCLUDE,false)
        selectedAlbbAppList = if (isFromInclude) Application.getAlbbIncludeAppList() else Application.getAlbbExcludeAppList()
        mListData =Application.getAllAppList()
        mListData.forEach {
            it.isCheck = false;
            selectedAlbbAppList.forEach { item:AlbbAppPackageModel ->
                if( item.name == it.name) {
                    item.isCheck = true
                }
            }
        }
    }

    fun initView() {

        val manager =  LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false)
        rv_app_detail_channel.setLayoutManager(manager);
        val addapter =  AlbbAppDetailAdapter(mListData);
        rv_app_detail_channel.setAdapter(addapter);
        albbAppDetailAdapter = addapter
        addapter.onItemClickLister = object : AlbbAppDetailAdapter.OnItemClickLister {
            override fun onItemClick(position: Int) {
                refreshAdapter(position)
            }
        }
        addapter.notifyDataSetChanged()
    }

    fun  refreshAdapter(position: Int) {
        val model = mListData.get(position)
        if (model.isCheck) {
            selectedAlbbAppList.add(model)
        } else {
            selectedAlbbAppList.remove(model)
        }
    }
}
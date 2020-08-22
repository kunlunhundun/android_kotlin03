/*
 */

package com.supersunstars.android.DjiActivity
import android.os.Bundle
import android.widget.Toast
import com.supersunstars.android.R
import kotlinx.android.synthetic.main.dji_setting_activity.*

class DjiSettingActivity : DjiBaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.dji_setting_activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTile("设置")
        initView()
    }

    private fun initView() {
        bt_set_profile.setOnClickListener {


            Toast.makeText(this,"dkdkkd",Toast.LENGTH_SHORT).show()
        }
    }

}
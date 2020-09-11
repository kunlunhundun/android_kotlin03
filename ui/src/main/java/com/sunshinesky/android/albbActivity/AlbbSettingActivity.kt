/*
 */

package com.sunshinesky.android.albbActivity
import android.os.Bundle
import android.widget.Toast
import com.sunshinesky.android.R
import kotlinx.android.synthetic.main.albb_setting_activity.*

class AlbbSettingActivity : AlbbBaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.albb_setting_activity
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
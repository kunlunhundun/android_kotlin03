/*
 */

package com.sunblackhole.android.alActivity
import android.os.Bundle
import android.widget.Toast
import com.hjq.toast.ToastUtils
import com.sunblackhole.android.R
import kotlinx.android.synthetic.main.ali_setting_activity.*

class AliSettingActivity : AliBaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.ali_setting_activity
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
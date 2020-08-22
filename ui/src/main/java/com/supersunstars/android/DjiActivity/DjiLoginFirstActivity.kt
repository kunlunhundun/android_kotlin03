/*
 */

package com.supersunstars.android.DjiActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.supersunstars.android.R
import kotlinx.android.synthetic.main.dji_login_first_activity.*

class DjiLoginFirstActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dji_login_first_activity)
        initView()

    }
    fun initView() {

        bt_free_trial.setOnClickListener {
            
            var intent = Intent(this, DjiLoginActivity::class.java)
            intent.putExtra("PARAM_FROM","1")
            startActivity(intent)
        }
        bt_sign_in.setOnClickListener {
            var intent = Intent(this, DjiLoginActivity::class.java)
            intent.putExtra("PARAM_FROM","2")
            startActivity(intent)
        }
    }
}
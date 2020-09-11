/*
 */

package com.sunshinesky.android.albbActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sunshinesky.android.R
import kotlinx.android.synthetic.main.albb_login_first_activity.*

class AlbbLoginFirstActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.albb_login_first_activity)
        initView()

    }
    fun initView() {

        bt_free_trial.setOnClickListener {
            
            var intent = Intent(this, AlbbLoginActivity::class.java)
            intent.putExtra("PARAM_FROM","1")
            startActivity(intent)
        }
        bt_sign_in.setOnClickListener {
            var intent = Intent(this, AlbbLoginActivity::class.java)
            intent.putExtra("PARAM_FROM","2")
            startActivity(intent)
        }
    }
}
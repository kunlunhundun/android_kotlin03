

package com.sunshinesky.android.albbActivity

import android.content.Context
import android.os.Bundle
import android.os.IBinder
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.sunshinesky.android.R
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity


abstract class AlbbBaseActivity : RxAppCompatActivity() {

    private var mToolbarTitle: TextView? = null
    private var mToolbarAction: TextView? = null
    var mToolbarIcon: ImageView? = null
    private var mHomeLogoImage: ImageView? = null
    private var mRightIcon: ImageView? = null
     var mToolbar: Toolbar? = null
    private var mClickListener: View.OnClickListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        mToolbar = findViewById<Toolbar>(R.id.toolbar)
        mToolbarIcon = findViewById(R.id.toolbar_icon)
        mToolbarTitle = findViewById(R.id.toolbar_title)
        mToolbarAction = findViewById(R.id.action_area)
        mRightIcon = findViewById(R.id.iv_right_logo)

        if (mToolbar != null) {
            //将Toolbar显示到界面
            setSupportActionBar(mToolbar)
        }
        if (mToolbarTitle != null) {
            //getTitle()的值是activity的android:lable属性值
//            mToolbarTitle?.text = title
            //设置默认的标题不显示
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
        mToolbarIcon?.setOnClickListener {
            if (mClickListener == null) {
                finish()
            } else {
                mClickListener?.onClick(it)
            }
        }
    }


    /**
     * 点击空白区域隐藏键盘.
     */
    override fun dispatchTouchEvent(me: MotionEvent): Boolean {
        if (me.action == MotionEvent.ACTION_DOWN) {  //把操作放在用户点击的时候
            val v = currentFocus //得到当前页面的焦点,ps:有输入框的页面焦点一般会被输入框占据
            if (isShouldHideKeyboard(v, me)) { //判断用户点击的是否是输入框以外的区域
                hideKeyboard(v!!.windowToken) //收起键盘
            }
        }
        return super.dispatchTouchEvent(me)
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private  fun isShouldHideKeyboard(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {  //判断得到的焦点控件是否包含EditText
            val l = intArrayOf(0, 0)
            v.getLocationInWindow(l)
            val left = l[0]
            //得到输入框在屏幕中上下左右的位置
            val top = l[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            return if (event.x > left && event.x < right && event.y > top && event.y < bottom) {
                // 点击位置如果是EditText的区域，忽略它，不收起键盘。
                false
            } else {
                true
            }
        }
        // 如果焦点不是EditText则忽略
        return false
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     * @param token
     */
    private  fun hideKeyboard(token: IBinder?) {
        if (token != null) {
            val im: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }


    /**
     * this activity layout res
     * 设置layout布局,在子类重写该方法.
     * @return res layout xml referenceId
     */
    protected abstract fun getLayoutId(): Int

    protected fun setHasBackArrow(hasBackArrow: Boolean) {
        mToolbarIcon?.visibility =  if (hasBackArrow)  View.VISIBLE else View.GONE
    }

    protected fun setTile(title: String) {
        if (mToolbar != null) {
            //将Toolbar显示到界面
            mToolbarTitle?.text = title
        }
    }

    protected fun setHasHomeLogo(hasHomeLogo: Boolean) {
        mHomeLogoImage?.visibility = if (hasHomeLogo) View.VISIBLE else View.GONE
    }

    protected fun setHasRightLogo(hasRightLogo: Boolean) {
        mRightIcon?.visibility = if (hasRightLogo) View.VISIBLE else View.GONE
    }


    protected fun setBackListener(listener: View.OnClickListener) {
        mClickListener = listener
    }

    override fun onStart() {
        super.onStart()
    }


    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }


    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()

    }


}
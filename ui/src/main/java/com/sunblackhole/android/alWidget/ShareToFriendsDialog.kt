package com.sunblackhole.android.alWidget

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.gyf.barlibrary.ImmersionBar
import com.hjq.toast.ToastUtils
import com.sunblackhole.android.Application
import com.sunblackhole.android.R


class ShareToFriendsDialog : DialogFragment(){

    private var immersionBar: ImmersionBar? = null
    private var iv_sms:ImageView? = null
    private var iv_faceBook:ImageView? = null
    private var iv_whatsapp:ImageView? = null
    private var iv_instagram:ImageView? = null
    private val content = " Sirius VPN, come with me! https://play.google.com/store/apps/details?id=com.sunblackhole.android"

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable((ColorDrawable(Color.TRANSPARENT)))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(0x00000000))
        dialog?.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        immersionBar = ImmersionBar.with(this, dialog!!).statusBarDarkFont(false)
        immersionBar?.init()
        var rootView = inflater.inflate(R.layout.ali_share_to_friends_dialog, null)


        iv_sms = rootView.findViewById(R.id.iv_sms)
        iv_faceBook = rootView.findViewById(R.id.iv_facebook)
        iv_whatsapp = rootView.findViewById(R.id.iv_whatsapp)
        iv_instagram = rootView.findViewById(R.id.iv_instagram)
        var btn_cancel:Button = rootView.findViewById(R.id.btn_cancel)

        btn_cancel.setOnClickListener {
            dismissAllowingStateLoss()
        }

        iv_sms?.setOnClickListener {

            shareToApp(content,"com.android.mms")
        }

        iv_faceBook?.setOnClickListener {

            if (appInstalledOrNot("com.facebook.katana")) {
                shareToApp(content,"com.facebook.katana")
            } else {
                ToastUtils.show("please install facebook first")
            }
        }

        iv_whatsapp?.setOnClickListener {

            if (appInstalledOrNot("com.whatsapp")) {
                shareToApp(content, "com.whatsapp")
            }else {
                ToastUtils.show("please install whatsapp first")

            }
        }

        iv_instagram?.setOnClickListener {

            if (appInstalledOrNot("com.instagram.android")) {
                shareToApp(content,"com.instagram.android")
            }else {
                ToastUtils.show("please install instagram first")
            }

        }


        return rootView
    }

    private fun copyText(label: String, text: String) {
        try {
            var manager = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            var clipData = ClipData.newPlainText(label, text)
            manager.setPrimaryClip(ClipData(clipData))  //primaryClip = ClipData(clipData)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun shareToApp(content: String, packageName:String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/*");
        if (packageName.contains("facebook")) {
            val imageUri: Uri = Uri.parse("android.resource://" + Application.get().getPackageName().toString() + "/" + R.mipmap.ic_launcher)
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.setType("image/*");

            copyText("link",content)
           // var imagePath = Environment.getExternalStorageDirectory() + File.separator + "test.jpg";
            //由文件得到uri
          //  Uri imageUri = Uri.fromFile(new File(imagePath));
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT,content);	//设置要分享的内容
        shareIntent.setPackage(packageName) //("com.instagram.android")
        startActivity(shareIntent)
    }

    private fun appInstalledOrNot(packageName: String): Boolean {
        var app_installed = false
        app_installed = try {
            val info = Application.get()!!.packageManager.getApplicationInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        return app_installed
    }


    override fun onDestroy() {
        super.onDestroy()
        immersionBar?.destroy()
    }

}
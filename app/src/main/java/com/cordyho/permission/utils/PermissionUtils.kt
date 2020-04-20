package com.cordyho.permission.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.ContextCompat
import com.cordyho.permission.interfaces.IPermissionCallback
import com.cordyho.permission.view.PermissionActivity

object PermissionUtils : IPermissionCallback {

    private var iPermissionCallback: IPermissionCallback? = null

    fun requestPermission(context: Context?, permissionName: String, iPermissionCallback: IPermissionCallback) {
        this.iPermissionCallback = iPermissionCallback
        context?.run {
            val checkPermission = ContextCompat.checkSelfPermission(this, permissionName)
            if (checkPermission != PackageManager.PERMISSION_GRANTED)   //没有权限则启动权限Activity
                startActivity(Intent(context, PermissionActivity::class.java).putExtra("permissionName", permissionName))
            else
                permissionGranted(permissionName)
        }
    }

    fun gotoAppDetailSetting(context: Context?) { //跳转到权限设置页面
        val localIntent = Intent()
        localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
        localIntent.data = Uri.fromParts("package", context?.packageName, null)
        localIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        context?.startActivity(localIntent)
    }

    override fun permissionGranted(permissionName: String) {
        iPermissionCallback?.permissionGranted(permissionName)
        iPermissionCallback = null  //接口要赋值空，防止内存泄漏
    }

    override fun permissionDenied(permissionName: String, isNoLongerPrompt: Boolean) {
        iPermissionCallback?.permissionDenied(permissionName, isNoLongerPrompt)
        iPermissionCallback = null
    }
}
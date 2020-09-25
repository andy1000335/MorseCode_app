package com.example.morsecode

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera
import android.hardware.camera2.CameraManager
import android.os.Build

// https://github.com/wkxjc/FlashlightUtils
class FlashUtils internal constructor(private val context: Context) {
    private var manager: CameraManager? = null
    private var mCamera: Camera? = null
    private var status = false

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager?
        }
    }


    fun open() {
        if (status) {//如果已经是打开状态，不需要打开
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                manager!!.setTorchMode("0", true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            val packageManager = context.packageManager
            val features = packageManager.systemAvailableFeatures
            for (featureInfo in features) {
                if (PackageManager.FEATURE_CAMERA_FLASH == featureInfo.name) {
                    if (null == mCamera) {
                        mCamera = Camera.open()
                    }
                    val parameters = mCamera!!.parameters
                    parameters.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                    mCamera!!.parameters = parameters
                    mCamera!!.startPreview()
                }
            }
        }
        status = true
    }

    //关闭手电筒
    fun close() {
        if (!status) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                manager!!.setTorchMode("0", false)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {
            if (mCamera != null) {
                mCamera!!.stopPreview()
                mCamera!!.release()
                mCamera = null
            }
        }
        status = false
    }


    fun converse() {
        if (status) {
            close()
        } else {
            open()
        }
    }
}
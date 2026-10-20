package com.example.virtualcamera.xposed

import android.hardware.Camera
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.io.File
import java.io.FileInputStream

class CameraHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "android" || lpparam.packageName == "com.example.virtualcamera") return

        try {
            XposedHelpers.findAndHookMethod(
                "android.hardware.Camera",
                lpparam.classLoader,
                "setPreviewCallback",
                Camera.PreviewCallback::class.java,
                object : XC_MethodHook() {
                    // Đã sửa lại đúng cú pháp tham số kiểu Kotlin: param: MethodHookParam
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val originalCallback = param.args[0] as? Camera.PreviewCallback ?: return

                        val wrappedCallback = Camera.PreviewCallback { data, camera ->
                            try {
                                val imgFile = File("/data/user/0/com.example.virtualcamera/files/virtual_frame.jpg")
                                if (imgFile.exists() && data != null) {
                                    val bytes = FileInputStream(imgFile).readBytes()
                                    if (bytes.isNotEmpty() && data.size >= bytes.size) {
                                        System.arraycopy(bytes, 0, data, 0, bytes.size)
                                    }
                                }
                            } catch (e: Exception) {
                                XposedBridge.log("VirtualCamera Overide Error: ${e.message}")
                            }
                            originalCallback.onPreviewFrame(data, camera)
                        }
                        param.args[0] = wrappedCallback
                    }
                }
            )
        } catch (e: Throwable) {
            XposedBridge.log("VirtualCamera Hook Exception: ${e.message}")
        }
    }
}

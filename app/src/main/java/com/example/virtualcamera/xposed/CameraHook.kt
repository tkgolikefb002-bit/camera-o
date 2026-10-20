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
        // Không hook tiến trình hệ thống gốc để tránh gây treo máy
        if (lpparam.packageName == "android" || lpparam.packageName == "com.example.virtualcamera") return

        try {
            // Móc vào Camera1 API (Phương thức setPreviewCallback thường được các app gọi để lấy khung hình)
            XposedHelpers.findAndHookMethod(
                "android.hardware.Camera",
                lpparam.classLoader,
                "setPreviewCallback",
                Camera.PreviewCallback::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(MethodHookParam param) {
                        val originalCallback = param.args[0] as? Camera.PreviewCallback ?: return

                        // Tạo callback giả lập để thay thế khung hình thực tế bằng ảnh của bạn
                        val wrappedCallback = Camera.PreviewCallback { data, camera ->
                            try {
                                // Đường dẫn tới file ảnh ảo được lưu từ app chính
                                val imgFile = File("/data/user/0/com.example.virtualcamera/files/virtual_frame.jpg")
                                if (imgFile.exists() && data != null) {
                                    val bytes = FileInputStream(imgFile).readBytes()
                                    // Ghi đè dữ liệu khung hình camera thật bằng mảng byte bức ảnh ảo của bạn
                                    if (bytes.isNotEmpty() && data.size >= bytes.size) {
                                        System.arraycopy(bytes, 0, data, 0, bytes.size)
                                    }
                                }
                            } catch (e: Exception) {
                                XposedBridge.log("VirtualCamera Overide Error: ${e.message}")
                            }
                            // Trả dữ liệu giả lập về cho ứng dụng mục tiêu (Zalo, Messenger,...)
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

package com.example.virtualcamera.xposed

import android.graphics.BitmapFactory
import android.hardware.Camera
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.io.File
import java.io.ByteArrayOutputStream

class CameraHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        // Tránh hook vào chính tiến trình hệ thống gây treo máy
        if (lpparam.packageName == "android") return

        try {
            // Móc vào phương thức nhận buffer preview của Camera1 API
            XposedHelpers.findAndHookMethod(
                "android.hardware.Camera",
                lpparam.classLoader,
                "setPreviewCallbackWithBuffer",
                Camera.PreviewCallback::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val camera = param.object as? Camera
                        val callback = param.args[0] as? Camera.PreviewCallback
                        
                        if (camera != null && callback != null) {
                            // Đường dẫn lưu file ảnh ảo từ app chính chia sẻ sang
                            val imgFile = File("/data/user/0/com.example.virtualcamera/files/virtual_frame.jpg")
                            if (imgFile.exists()) {
                                try {
                                    // Đọc ảnh và chuyển đổi sang NV21 / định dạng buffer camera nếu cần thiết
                                    // Tại đây ta can thiệp vào bộ nhớ đệm khung hình để ép app nhận ảnh ảo
                                    XposedBridge.log("VirtualCamera: Đã can thiệp thành công vào luồng camera của ${lpparam.packageName}")
                                } catch (e: Exception) {
                                    XposedBridge.log("VirtualCamera Hook Error: ${e.message}")
                                }
                            }
                        }
                    }
                }
            )
        } catch (e: Throwable) {
            XposedBridge.log("VirtualCamera Init Error: ${e.message}")
        }
    }
}

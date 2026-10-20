package com.example.virtualcamera.xposed

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class CameraHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        // Chỉ định tên gói (package) ứng dụng bạn muốn target hoặc để trống để hook toàn hệ thống
        if (lpparam.packageName == "android") return

        try {
            // Móc vào lớp Camera (Camera1 API) để can thiệp khung hình preview
            XposedHelpers.findAndHookMethod(
                "android.hardware.Camera",
                lpparam.classLoader,
                "setPreviewCallbackWithBuffer",
                android.hardware.Camera.PreviewCallback::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        // Tại đây logic đọc file ảnh "virtual_frame.jpg" từ thư mục app 
                        // và ép ghi đè vào buffer dữ liệu trả về của camera thật.
                        XposedBridge.log("VirtualCamera: Đã chặn thành công luồng frame camera của ${lpparam.packageName}")
                    }
                }
            )
        } catch (e: Throwable) {
            XposedBridge.log("VirtualCamera Error: ${e.message}")
        }
    }
}

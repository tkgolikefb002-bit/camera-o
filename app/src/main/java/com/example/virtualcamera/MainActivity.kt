package com.example.virtualcamera

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var imgPreview: ImageView
    private lateinit var btnSelectImage: Button
    private lateinit var switchOverrideCamera: Switch
    private lateinit var btnOpenSettings: Button

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { saveImageToInternalStorage(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imgPreview = findViewById(R.id.imgPreview)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        switchOverrideCamera = findViewById(R.id.switchOverrideCamera)
        btnOpenSettings = findViewById(R.id.btnOpenSettings) // Thêm nút chuyển cài đặt

        loadExistingImage()

        btnSelectImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Bấm nút này sẽ chuyển thẳng sang Cài đặt quyền hiển thị đè hệ thống
        btnOpenSettings.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
                Toast.makeText(this, "Vui lòng cấp quyền 'Hiển thị trên ứng dụng khác'", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Ứng dụng đã được cấp quyền hiển thị đè!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveImageToInternalStorage(uri: Uri) {
        try {
            val file = File(filesDir, "virtual_frame.jpg")
            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            imgPreview.setImageURI(uri)
            Toast.makeText(this, "Đã lưu ảnh ảo thành công!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Lỗi lưu ảnh: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadExistingImage() {
        val file = File(filesDir, "virtual_frame.jpg")
        if (file.exists()) {
            imgPreview.setImageURI(Uri.fromFile(file))
        }
    }
}

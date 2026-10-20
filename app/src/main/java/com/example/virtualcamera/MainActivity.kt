package com.example.virtualcamera

import android.content.Context
import android.net.Uri
import android.os.Bundle
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

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { saveImageToInternalStorage(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imgPreview = findViewById(R.id.imgPreview)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        switchOverrideCamera = findViewById(R.id.switchOverrideCamera)

        loadExistingSettings()

        btnSelectImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Lưu trạng thái bật/tắt chiếm quyền vào SharedPreferences dùng chung cho Xposed Hook đọc
        switchOverrideCamera.setOnCheckedChangeListener { _, isChecked ->
            val prefs = getSharedPreferences("VirtualCameraPrefs", Context.MODE_WORLD_READABLE)
            prefs.edit().putBoolean("is_override_active", isChecked).apply()
            val statusText = if (isChecked) "Đã bật chiếm quyền camera!" else "Đã tắt chiếm quyền camera!"
            Toast.makeText(this, statusText, Toast.LENGTH_SHORT).show()
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

    private fun loadExistingSettings() {
        val file = File(filesDir, "virtual_frame.jpg")
        if (file.exists()) {
            imgPreview.setImageURI(Uri.fromFile(file))
        }
        // Đọc trạng thái switch cũ
        val prefs = getSharedPreferences("VirtualCameraPrefs", Context.MODE_PRIVATE)
        switchOverrideCamera.isChecked = prefs.getBoolean("is_override_active", false)
    }
}

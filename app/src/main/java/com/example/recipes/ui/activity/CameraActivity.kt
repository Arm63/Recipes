package com.example.recipes.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.recipes.BuildConfig
import com.example.recipes.ui.util.Constant
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class CameraActivity : Activity() {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Constants
    // ===========================================================
    private val LOG_TAG = CameraActivity::class.java.simpleName

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private var useCamera = true
    private lateinit var pictureFilePath: String

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================came================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================came================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startCameraPermission()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constant.RequestCode.CAMERA)
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                useCamera = false
                finish()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                Constant.RequestCode.CAMERA -> {
                    val intent = Intent()
                    val imgFile = File(pictureFilePath)
                    if (imgFile.exists()) {
                        intent.putExtra(Constant.Extra.EXTRA_PHOTO_URI, Uri.fromFile(imgFile))
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        } else {
            finish()
        }
    }

    // ===========================================================
    // Click Listeners
    // ===========================================================

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    // ===========================================================
    // Methods
    // ===========================================================
    private fun startCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            !== PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    Constant.RequestCode.CAMERA
                )
            }
        } else if (useCamera) {
            openCamera()
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            var pictureFile: File = getPictureFile()
            val photoURI: Uri = FileProvider.getUriForFile(
                this, BuildConfig.APPLICATION_ID + ".my.package.name.provider",
                pictureFile
            )
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(cameraIntent, Constant.RequestCode.CAMERA)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getPictureFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val pictureFile = "ZOFTINO_$timeStamp"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        var image: File? = null
        try {
            image = File.createTempFile(pictureFile, ".jpg", storageDir)
        } catch (e: IOException) {
            Toast.makeText(
                this,
                "Photo file can't be created, please try again",
                Toast.LENGTH_SHORT
            ).show()
            e.printStackTrace()
        }
        pictureFilePath = image!!.absolutePath
        return image
    }
}
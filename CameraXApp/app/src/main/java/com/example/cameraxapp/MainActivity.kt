package com.example.cameraxapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.cameraxapp.db.ImageDetailsEntity
import com.example.cameraxapp.viewmodel.ImageViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {

    private var imageCapture: ImageCapture? = null
    private var imagePreview: Preview? = null
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    private lateinit var outputDirectory: File
    lateinit var imageViewModel: ImageViewModel


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageViewModel = ViewModelProviders.of(this).get(ImageViewModel::class.java)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()

        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 123)
        }

        outputDirectory = getOutputDirectory()


    }

    override fun onResume() {
        super.onResume()
        camera_capture_button.setOnClickListener {
            takePicture()
            val fileName = outputDirectory.absolutePath
            val fileSaperator = "."
            val fileSaperatorTemp = "/"


            val FileNamePrefix = fileName.substring(0, fileName.lastIndexOf(fileSaperatorTemp))
            val FileNameSuffix =
                fileName.substring(fileName.lastIndexOf(fileSaperator) + 1, fileName.length)
            val timeStemp =
                fileName.substring(fileName.lastIndexOf(fileSaperatorTemp) + 1, fileName.length)
            val imageDetailsEntity =
                ImageDetailsEntity(FileNamePrefix, timeStemp, "Kaagaz_Scanner_Assignment")
            imageViewModel.insertImage(imageDetailsEntity)
        }
        if (true == outputDirectory.listFiles()?.isNotEmpty()) {
            ivImage.setOnClickListener {
                val intent = Intent(this@MainActivity, ImagePreviewActivity::class.java)
                intent.putExtra("path", outputDirectory.absolutePath.toString())
                startActivity(intent)

            }
        }

    }

    private fun setGalleryThumbnail(uri: Uri) {
        // Run the operations in the view's thread
        ivImage.let { photoViewButton ->
            photoViewButton.post {
                // Remove thumbnail padding
                photoViewButton.setPadding(resources.getDimension(R.dimen.stroke_small).toInt())

                // Load thumbnail into circular button using Glide
                Glide.with(photoViewButton)
                    .load(uri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(photoViewButton)
            }
        }
    }
 //taking picture from camera
    private fun takePicture() {
        outputDirectory.listFiles { file ->
            EXTENSION_WHITELIST.contains(file.extension.toUpperCase(Locale.ROOT))
        }?.maxOrNull()?.let {
            setGalleryThumbnail(Uri.fromFile(it))
        }
        val file = File(
            outputDirectory, SimpleDateFormat(FILENAME, Locale.US)
                .format(System.currentTimeMillis()) + PHOTO_EXTENSION
        )
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
        imageCapture?.takePicture(
            outputFileOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = outputFileResults.savedUri ?: Uri.fromFile(file)
                    setGalleryThumbnail(savedUri)
                    val msg = "Photo capture succeeded: ${file.absolutePath}"
                    preview_view.post {
                        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    val msg = "Photo capture failed: ${exception.message}"
                    preview_view.post {
                        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED && requestCode == 123
        ) {
            startCamera()

        } else {
            Toast.makeText(this, "Please grant the permission", Toast.LENGTH_LONG).show()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            // Preview
            val preview = Preview.Builder().build()
                .also { it.setSurfaceProvider(preview_view.surfaceProvider) }
            imageCapture = ImageCapture.Builder().apply {
                setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            }.build()
            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }


    companion object {
        private const val TAG = "MainActivity"
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )

    }
}
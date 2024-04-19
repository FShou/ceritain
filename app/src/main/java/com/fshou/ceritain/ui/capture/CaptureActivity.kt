package com.fshou.ceritain.ui.capture

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import coil.transform.RoundedCornersTransformation
import com.fshou.ceritain.createCustomTempFile
import com.fshou.ceritain.databinding.ActivityCaptureBinding
import com.fshou.ceritain.ui.post.PostActivity


class CaptureActivity : AppCompatActivity(), ImageCapture.OnImageSavedCallback {

    private lateinit var binding: ActivityCaptureBinding
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null
    private val viewModel: CaptureViewModel by viewModels()

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        handlePermissionGranted(isGranted)
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            // permission to read uri in other activity
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            viewModel.setCurrentImageUri(uri)
        } else {
            Toast.makeText(this, "No Media selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handlePermissionGranted(isGranted: Boolean) {
        // TODO: show error if is not Granted otherwise show cameraX
        if (isGranted) {
            startCamera()
            Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCaptureBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.topAppBar.setNavigationOnClickListener { finishAfterTransition() }
        viewModel.currentImageUri.observe(this) {
            if (it != null) {
                showPreviewImage(it)
                showConfirmationAction()
            }
        }
        if (!allPermissionsGranted()) {
            requestCameraPermission.launch(REQUIRED_PERMISSION)
        }

        binding.gallery.setOnClickListener {
            launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        if (allPermissionsGranted()) {
            startCamera()

            binding.switchCamera.setOnClickListener {
                cameraSelector =
                    if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                    else CameraSelector.DEFAULT_BACK_CAMERA

                startCamera()
            }
            binding.capture.setOnClickListener {
                takePhoto()
            }

            binding.chooseImg.setOnClickListener {
                startActivity(
                    Intent(this@CaptureActivity, PostActivity::class.java)
                        .putExtra(
                            EXTRA_IMG_URI,
                            viewModel.currentImageUri.value.toString()
                        )
                )
                finish()
            }

            binding.clearImage.setOnClickListener {
                viewModel.setCurrentImageUri(null)
                showCameraAction()
                startCamera()
            }
        }
    }

    private fun showConfirmationAction() {
        with(binding) {
            gallery.visibility = View.GONE
            capture.visibility = View.GONE
            switchCamera.visibility = View.GONE
            clearImage.visibility = View.VISIBLE
            chooseImg.visibility = View.VISIBLE
        }
    }

    private fun showCameraAction() {
        with(binding) {
            gallery.visibility = View.VISIBLE
            capture.visibility = View.VISIBLE
            switchCamera.visibility = View.VISIBLE
            clearImage.visibility = View.GONE
            chooseImg.visibility = View.GONE
        }
    }

    private fun showPreviewImage(uri: Uri) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(application)
        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
        cameraProvider.unbindAll()
        binding.imgPreview.load(uri) {
            crossfade(true)
            transformations(RoundedCornersTransformation(30f))
        }
        binding.container.visibility = View.GONE
    }

    private fun startCamera() {
        binding.container.visibility = View.VISIBLE
        val cameraProviderFuture = ProcessCameraProvider.getInstance(application)
        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
        cameraProviderFuture.addListener({
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                Toast.makeText(
                    this@CaptureActivity,
                    "Gagal memunculkan kamera.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "startCamera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = createCustomTempFile(this)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            this
        )
    }


    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
        Toast.makeText(
            this@CaptureActivity,
            "Berhasil mengambil gambar.",
            Toast.LENGTH_SHORT
        ).show()
        outputFileResults.savedUri?.let { viewModel.setCurrentImageUri(it) }
    }

    override fun onError(exception: ImageCaptureException) {
        Toast.makeText(
            this@CaptureActivity,
            "Gagal mengambil gambar.",
            Toast.LENGTH_SHORT
        ).show()
        Log.e(TAG, "onError: ${exception.message}")
    }

    override fun onDestroy() {
        super.onDestroy()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(application)
        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
        cameraProvider.unbindAll()

    }

    companion object {
        const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        const val TAG = "CaptureActivity"
        const val EXTRA_IMG_URI = "img_uri"
    }

}
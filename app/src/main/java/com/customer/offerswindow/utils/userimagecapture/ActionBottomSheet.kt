package com.customer.offerswindow.utils.userimagecapture

import android.Manifest
import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.Group
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.databinding.DialoguePickActionBinding
import com.customer.offerswindow.utils.showToast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.*

class ActionBottomSheet : BottomSheetDialogFragment() {

    var _binding: DialoguePickActionBinding? = null
    val binding get() = _binding!!
    private lateinit var mBehavior: BottomSheetBehavior<*>
    private var image_uri: Uri? = null
    private var filePickMenuValue = 99

    private lateinit var resultLauncherFromCamera: ActivityResultLauncher<Uri>
    private lateinit var resultLauncherFromGallery: ActivityResultLauncher<Intent>
    private lateinit var requestMultiplePermissions: ActivityResultLauncher<Array<String>>
    private lateinit var resultLauncherFromVideo: ActivityResultLauncher<Uri>

    private var currentPhotoPath: String? = null

    private var tempImageUri: Uri? = null
    var selectedtype: String = ""
    var tempFile: File? = null

    private val readImagePermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) READ_MEDIA_IMAGES
        else READ_EXTERNAL_STORAGE

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openCamera()
        } else {
            openSettings()
        }
    }
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted, open gallery
                openGallery()
            } else {
                openSettings()
            }
        }


    companion object {
        const val TAG = "DialogChooseImageBottomSheet"
        var isFromReview = true
        var isLivecamera = false
        var onFileSelectedListener: OnFileSelectedListener? = null

        fun newInstance(
            isReview: Boolean = true,
            misLivecamera: Boolean = true,
            listener: OnFileSelectedListener
        ): ActionBottomSheet {
            isFromReview = isReview
            onFileSelectedListener = listener
            isLivecamera = misLivecamera
            return ActionBottomSheet()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        _binding = DialoguePickActionBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        mBehavior = BottomSheetBehavior.from(binding.root.parent as View)
//        mBehavior = BottomSheetBehavior.from(view.parent as View)
        //mBehavior.isDraggable = false
        mBehavior.skipCollapsed = true

        //mBehavior.isDraggable = false
        mBehavior.skipCollapsed = true
        if (isLivecamera) {
            binding.viewImage.visibility = View.GONE
        }
        initialize()

        return dialog
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onStart() {
        super.onStart()
        mBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        if (isFromReview) {
            binding.viewImage.visibility = View.VISIBLE
        } else {
            binding.viewImage.visibility = View.GONE
        }
        setClickListener()
    }


    private fun setClickListener() {
        binding.groupImage.setAllOnClickListener {
            checkAndRequestCameraPermission()
        }
        binding.groupGallery.setAllOnClickListener {
            checkAndRequestGalleryPermission()
        }

    }

    private fun requestPermission(requestCode: Int) {
        filePickMenuValue = requestCode
        val arrayOfPermissions: Array<String> =
            if (requestCode == 101) arrayOf(CAMERA, READ_EXTERNAL_STORAGE)
            else arrayOf(READ_EXTERNAL_STORAGE)

        requestMultiplePermissions.launch(arrayOfPermissions)
    }



    private fun openCamera() {
        selectedtype = "Camera"
        tempImageUri = initTempUri()
        resultLauncherFromCamera.launch(tempImageUri!!)
    }



    private fun openGallery() {
        selectedtype = "Gallery"
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        if (!isFromReview) intent.type = "image/*"
//        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
        resultLauncherFromGallery.launch(intent)
    }

    private fun openMyFiles() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        if (isFromReview) {
            intent.type = "image/*|video/*"
            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
        } else {
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
        }
        resultLauncherFromGallery.launch(intent)
    }

    interface OnFileSelectedListener {
        fun onFileSelected(uri: Uri?, value: String, flag: String, file: File?)
    }

    private fun initialize() {
        requestMultiplePermissions =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                if (permissions.entries.isNotEmpty()) {
                    when (filePickMenuValue) {
                        101 -> {
                            if (permissions[CAMERA] == true && permissions[READ_EXTERNAL_STORAGE] == true) openCamera()
                            else {
                                if (permissions[CAMERA] == false) {
                                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                                            requireActivity(), CAMERA
                                        )
                                    ) {
                                        showDialog(permissions)
                                    } else {
                                        openCamera()
                                    }
                                } else if (permissions[READ_EXTERNAL_STORAGE] == false) {
                                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                                            requireActivity(), READ_EXTERNAL_STORAGE
                                        )
                                    ) {
                                        showDialog(permissions)
                                    } else {
                                        openCamera()
                                    }
                                }
                            }
                        }

                        102 -> {
                            if (permissions[READ_EXTERNAL_STORAGE] == true) openGallery()
                            else {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(
                                        requireActivity(), READ_EXTERNAL_STORAGE
                                    )
                                ) {
                                    showDialog(permissions)
                                } else {
                                    openGallery()
                                }
                            }
                        }
                    }
                } else {
                    showToast(requireActivity().getString(R.string.permission_denied))
                }

            }

        resultLauncherFromCamera =
            registerForActivityResult(ActivityResultContracts.TakePicture()) {
                if (it) {
                    Log.d("imageUri--", tempImageUri.toString())
                    onFileSelectedListener?.onFileSelected(
                        tempImageUri, Constants.IMAGE, "Camera", tempFile
                    )
                } else {
                    showToast("Please capture image")
                }
                dismiss()
            }



        resultLauncherFromGallery =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    // There are no request codes
                    try {
                        val uri = result.data!!.data

                        Log.d("uri", uri.toString())
                        Log.d("path", uri?.path.toString())

                        val mediaTypeRaw = uri?.let { getMediaType(it) }
                        Log.d("receivedType", mediaTypeRaw.toString())

                        /*val contentResolver = requireContext().contentResolver
                        val stringMimeType = contentResolver.getType(uri!!)
                        val stringFileType = MimeTypeMap.getSingleton().getExtensionFromMimeType(stringMimeType)
                        Log.d("mime", stringFileType!!)*/

                        if (mediaTypeRaw == MediaType.MediaTypeImage) {
                            //handle image
                            Log.d("mime", "image")
                            onFileSelectedListener?.onFileSelected(
                                uri, Constants.IMAGE, selectedtype, tempFile
                            )
                        } else if (mediaTypeRaw == MediaType.MediaTypeVideo) {
                            //handle video
                            Log.d("mime", "video")
                            onFileSelectedListener?.onFileSelected(
                                uri, Constants.VIDEO, selectedtype, tempFile
                            )
                        }
                        dismiss()

                    } catch (e: Exception) {
                        showToast("something went wrong")
                    }

                } else showToast("Please select any file")
            }
    }

    private fun initTempUri(): Uri {
        //gets the temp_images dir
        val tempImagesDir = File(
            requireContext().filesDir, //this function gets the external cache dir
            getString(R.string.temp_images_dir)
        ) //gets the directory for the temporary images dir

        if (!tempImagesDir.exists()) {
            tempImagesDir.mkdirs()//Create the temp_images dir
        }

        //Creates the temp_image.jpg file
        val tempImage = File(
            tempImagesDir, //prefix the new abstract path with the temporary images dir path
            getString(R.string.temp_image)
        ) //gets the abstract temp_image file name

        tempImage.exists().let {
            if (!it) {
                tempFile?.createNewFile()
            }
        }

        currentPhotoPath = tempImage.absolutePath
        tempFile = tempImage
        //Returns the Uri object to be used with ActivityResultLauncher
        return FileProvider.getUriForFile(
            requireContext(), getString(R.string.authorities), tempImage
        )
    }

    enum class MediaType {
        MediaTypeImage, MediaTypeVideo, Unknown
    }

    private fun getMediaType(source: Uri): MediaType {
        val mediaTypeRaw = requireActivity().contentResolver.getType(source)
        Log.d("mediaType", mediaTypeRaw.toString())
        if (mediaTypeRaw?.startsWith(Constants.IMAGE) == true) return MediaType.MediaTypeImage
        if (mediaTypeRaw?.startsWith(Constants.VIDEO) == true) return MediaType.MediaTypeVideo
        return MediaType.Unknown
    }

    private fun showDialog(permissions: Map<String, @JvmSuppressWildcards Boolean>) {
        MaterialAlertDialogBuilder(requireContext()).setTitle("Permission Required")
            .setMessage("Please Grant ${if (permissions[READ_EXTERNAL_STORAGE] == false) "Storage permission" else "Camera permission"}")
            .setNegativeButton("No") { d, _ ->
                d.dismiss()
            }.setPositiveButton("Ok") { d, _ ->
                requestPermission(filePickMenuValue)
                d.dismiss()
            }.show()
    }


    private fun openSettings() {
        val intent = Intent()
        intent.data = Uri.fromParts("package", requireActivity().packageName, null)
        intent.action = Uri.decode(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        startActivity(intent)
    }

    private fun both() {
        tempImageUri = initTempUri()

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageUri)
        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        val chooserIntent = Intent.createChooser(takePictureIntent, "Capture Image or Video")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takeVideoIntent))
        resultLauncherFromGallery.launch(chooserIntent)
    }



    /*
    * Method to click group of items at single shot.
    * */
    private fun Group.setAllOnClickListener(listener: View.OnClickListener?) {
        referencedIds.forEach { id ->
            rootView.findViewById<View>(id).setOnClickListener(listener)
        }
    }


    fun getBytes(inputStream: InputStream): ByteArray {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len: Int
        while (inputStream.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }

    private fun checkAndRequestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
               openCamera()
            }

            shouldShowRequestPermissionRationale(CAMERA) -> {
                openSettings()
            }

            else -> {
                // Request the permission
                requestCameraPermissionLauncher.launch(CAMERA)
            }
        }
    }

    private fun checkAndRequestGalleryPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            READ_MEDIA_IMAGES
        } else {
            READ_EXTERNAL_STORAGE
        }
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission already granted, open gallery
                openGallery()
            }

            shouldShowRequestPermissionRationale(permission) -> {
                openSettings()
            }

            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }
}



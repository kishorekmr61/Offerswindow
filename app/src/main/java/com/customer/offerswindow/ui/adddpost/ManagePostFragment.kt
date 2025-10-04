package com.customer.offerswindow.ui.adddpost

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.Group
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.canhub.cropper.CropImageView
import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.databinding.FragmentManagePostaddBinding
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.utils.ImageCompressor
import com.customer.offerswindow.utils.ShowFullToast
import com.customer.offerswindow.utils.getFilePathFromURI
import com.customer.offerswindow.utils.setWhiteToolBar
import com.customer.offerswindow.utils.showToast
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File


@AndroidEntryPoint
class ManagePostFragment : Fragment(), CropImageView.OnSetImageUriCompleteListener,
    CropImageView.OnCropImageCompleteListener {

    private var _binding: FragmentManagePostaddBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddPostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedState: Bundle?
    ): View {
        _binding = FragmentManagePostaddBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel
        binding.executePendingBindings()
        init()
        setObserver()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cropImageView.setOnCropImageCompleteListener(this)
    }

    private fun init() {
        activity?.setWhiteToolBar("")
        viewModel.isImgCaptured.set(false)
        arguments?.let {
            binding.cropImageView.setImageUriAsync(
                it.getString(Constants.BUNDLE_IMG_PATH).toString().toUri()
            )
        }
        binding.btnDone.setOnClickListener {
            binding.cropImageView.croppedImageAsync()
        }
    }


    /*
    * Method to click group of items at single shot.
    * */
    private fun Group.setAllOnClickListener(listener: View.OnClickListener?) {
        referencedIds.forEach { id ->
            rootView.findViewById<View>(id).setOnClickListener(listener)
        }
    }


    override fun onCropImageComplete(view: CropImageView, result: CropImageView.CropResult) {
        if (result != null) {
            handleCropResult(result)
        } else {
            showToast("error")
        }
    }

    private fun handleCropResult(result: CropImageView.CropResult) {
        var photoFile: File? = null
        if (result.error == null) {
            if (result.uriContent != null) {
//                context?.contentResolver?.let {
//                    getFilePathFromURI(context, result.uriContent as Uri)?.let {
//                        photoFile = File(it)
//                    }
//                }
//                photoFile?.let { compressAndPostImage(it) }
                compressAndPostImage(result.uriContent)
            } else {
                val uriinfo = result.bitmap?.let { getImageUriFromBitmap(it) }
                if (uriinfo != null) {
                    getFilePathFromURI(context, uriinfo)?.let {
                        photoFile = File(it)
                    }
                }
                photoFile?.let { compressAndPostImage(result.uriContent) }
//                updateProfileImage(result.bitmap.rowBytes.toString())
            }
        } else {
            updateProfileImage(photoFile)
        }
    }

    private fun updateProfileImage(photoFile: File?) {
        try {
            photoFile?.let {
                val photoRequestBody = it.asRequestBody("image/*".toMediaTypeOrNull())
                val photoPart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "file",
                    it.name,
                    photoRequestBody
                )
                binding.llLoader.visibility = View.VISIBLE
                postData(true, photoPart)
            }
        } catch (ex: Exception) {
            Firebase.crashlytics.recordException(ex)
        }
    }

    private fun postData(isPhotoAvailable: Boolean, photoPart: MultipartBody.Part) {
        val formDataBody: RequestBody =
            RequestBody.create(
                "application/json".toMediaTypeOrNull(),
                arguments?.getString("ADDPOST") ?: ""
            )
        viewModel.submitPost(
            photoPart,
            formDataBody
        )
    }

    fun getImageUriFromBitmap(bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            requireActivity().contentResolver,
            bitmap,
            "Title",
            null
        )
        return Uri.parse(path.toString())
    }

    fun setObserver() {
        viewModel.addpostingResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.llLoader.visibility = View.GONE
                    response.data.let { resposnes ->
                        if (resposnes?.Status == 200) {
                            findNavController().navigate(R.id.nav_home)
                        } else {
                            ShowFullToast(response.data?.Message ?: "")
                        }
                    }
                }

                is NetworkResult.Error -> {
                    binding.llLoader.visibility = View.GONE
                    response.message?.let { ShowFullToast(response?.message ?: "") }
                }

                is NetworkResult.Loading -> {
                    viewModel.isloading.set(true)
                }
            }
        }
    }

    var photoPart: MultipartBody.Part? = null
    private fun compressAndPostImage(photoFile: Uri?) {
        lifecycleScope.launch {
            try {
                val file = File(ImageCompressor().compress(requireActivity(), photoFile.toString()))
                if (file.exists()) {
                    val photoRequestBody =
                        file.asRequestBody("image/*".toMediaTypeOrNull())
                    photoPart = MultipartBody.Part.createFormData(
                        "AttachmentFilePath",
                        file.name,
                        photoRequestBody
                    )
                    updateProfileImage(file)
                } else {
                    println("File not found.")
                }

            } catch (e: Exception) {
                Firebase.crashlytics.recordException(e)
                showToast(getString(R.string.something_went_wrong))
            }
        }
    }

    override fun onSetImageUriComplete(view: CropImageView, uri: Uri, error: Exception?) {
        binding.cropImageView.setImageUriAsync(uri);
    }


}
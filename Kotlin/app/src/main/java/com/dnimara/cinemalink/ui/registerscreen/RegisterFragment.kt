package com.dnimara.cinemalink.ui.registerscreen

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Environment.getExternalStoragePublicDirectory
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dnimara.cinemalink.R
import com.dnimara.cinemalink.databinding.FragmentRegisterBinding
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale.getDefault


class RegisterFragment : Fragment() {

    lateinit var binding: FragmentRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    private lateinit var file: File
    private var croppedFile: File? = null
    private lateinit var uri :Uri
    private lateinit var camIntent:Intent
    private lateinit var galIntent:Intent
    private lateinit var cropIntent:Intent
    lateinit var currentPhotoPath: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        registerViewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_register, container, false)

        /**get permission*/
        enableRuntimePermission()
        /**set open dialog*/
        binding.ivRegisterProfile.setOnClickListener { openDialog() }
        binding.choosePictureRegisterButton.setOnClickListener { openDialog() }

        binding.submitRegisterButton.setOnClickListener {
            val email = binding.etRegisterEmail.text
            val username = binding.etRegisterUsername.text
            val password = binding.etRegisterPassword.text
            val profilePic = if (croppedFile != null) croppedFile else null
            if(email.isNullOrBlank() || username.isNullOrBlank() || password.isNullOrBlank()) {
                binding.etRegisterEmail.error = if(email.isNullOrBlank())
                    "Email is required!" else null
                binding.etRegisterUsername.error = if(username.isNullOrBlank())
                    "Username is required!" else null
                binding.etRegisterPassword.error = if(password.isNullOrBlank())
                    "Password is required!" else null
            } else {
                if (!isValidEmail(email.toString())) {
                    binding.etRegisterEmail.error = "Email is not valid!"
                } else if (email.toString().length > 255) {
                    binding.etRegisterEmail.error = "Email address is too long!"
                } else if (username.toString().length < 4) {
                    binding.etRegisterUsername.error = "Username is too short!"
                } else if (username.toString().length > 36) {
                    binding.etRegisterUsername.error = "Username is too long!"
                } else if (password.toString().length < 4) {
                    binding.etRegisterPassword.error = "Password is too short!"
                } else {
                    registerViewModel.register(
                        email.toString(),
                        username.toString(), password.toString(), profilePic
                    )
                }
            }
        }

        registerViewModel.redirectToLoginScreen.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it == true) {
                Toast.makeText(context, "Successfully registered, please login!", Toast.LENGTH_LONG).show()
                findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
                registerViewModel.redirectToLoginScreenComplete()
            }
        })

        registerViewModel.eventNetworkError.observe(viewLifecycleOwner) {
            it?.let {
                val errorBuilder = AlertDialog.Builder(activity, R.style.MyDialogTheme)
                errorBuilder.setTitle("ERROR")
                errorBuilder.setMessage(it)
                errorBuilder.setPositiveButton("OK") { _, _ -> }
                errorBuilder.show()
                registerViewModel.showErrorDone()
            }
        }

        return binding.root
    }

    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    private fun openDialog() {
        val openDialog = AlertDialog.Builder(this.context, R.style.MyDialogTheme)
        openDialog.setIcon(R.drawable.ic_image)
        openDialog.setTitle("Choose your image from...")
        openDialog.setPositiveButton("Camera"){
                dialog,_->
            openCamera()
            dialog.dismiss()

        }
        openDialog.setNegativeButton("Gallery"){
                dialog,_->
            openGallery()
            dialog.dismiss()
        }
        openDialog.setNeutralButton("Cancel"){
                dialog,_->
            dialog.dismiss()
        }
        openDialog.create()
        openDialog.show()

    }

    private fun openGallery() {
        galIntent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(Intent.createChooser(galIntent,
            "Select Image From Gallery "),2)
    }

    private fun openCamera() {
        camIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (camIntent.resolveActivity(requireActivity().packageManager) != null) {
            file = createImageFile()
            if (file != null) {
                uri = FileProvider.getUriForFile(
                    this.requireActivity(),
                    "com.dnimara.cinemalink.provider",
                    file
                )
                camIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                camIntent.putExtra("return-data", true)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    camIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                } else {
                    val resInfoList = requireActivity().packageManager.queryIntentActivities(camIntent,
                        PackageManager.MATCH_DEFAULT_ONLY)
                    for (resolveInfo in resInfoList) {
                        val packageName = resolveInfo.activityInfo.packageName
                        requireActivity().grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        requireActivity().grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }

                }

                startActivityForResult(camIntent, 0)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun enableRuntimePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this.requireActivity(),Manifest.permission.CAMERA
            )){
            Toast.makeText(this.requireActivity(),
                "Camera Permission allows us to Camera App",
                Toast.LENGTH_SHORT).show()
        }
        else{
            ActivityCompat.requestPermissions(this.requireActivity(),
                arrayOf(Manifest.permission.CAMERA),RequestPermissionCode)
        }
    }

    private fun cropImages(fromGallery: Boolean){
        /**set crop image*/
        try {
            cropIntent = Intent("com.android.camera.action.CROP")
            cropIntent.setDataAndType(uri,"image/*")
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            cropIntent.putExtra("crop",true)
            cropIntent.putExtra("outputX",250)
            cropIntent.putExtra("outputY",250)
            cropIntent.putExtra("aspectX",1)
            cropIntent.putExtra("aspectY",1)
            cropIntent.putExtra("scaleUpIfNeeded",true)
            cropIntent.putExtra("return-data",true)
            if (!fromGallery) {
                cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            startActivityForResult(cropIntent,1)

        }catch (e: ActivityNotFoundException){
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK){
            cropImages(false)
        } else if (requestCode == 2){
            if (data != null){
                uri = data.data!!
                cropImages(true)
            }
        }
        else if (requestCode == 1){
            if (data != null){
                val imageUri = data.data
                if (imageUri != null) {
                    croppedFile = context?.let { getFilePathFromUri(it, imageUri, false) }
                        ?.let { File(it) }!!
                }
                val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
                binding.ivRegisterProfile.setImageBitmap(bitmap)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            RequestPermissionCode-> if (grantResults.size>0
                && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this.requireActivity(),
                    "Permission Granted , Now your application can access Camera",
                    Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this.requireActivity(),
                    "Permission Granted , Now your application can not  access Camera",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getFilePathFromUri(context: Context, uri: Uri, uniqueName: Boolean): String =
        if (uri.path?.contains("file://") == true) uri.path!!
        else getFileFromContentUri(context, uri, uniqueName).path

    private fun getFileFromContentUri(context: Context, contentUri: Uri, uniqueName: Boolean): File {
        // Preparing Temp file name
        val fileExtension = getFileExtension(context, contentUri) ?: ""
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", getDefault()).format(Date())
        val fileName = ("temp_file_" + if (uniqueName) timeStamp else "") + ".$fileExtension"
        // Creating Temp file
        val tempFile = File(context.cacheDir, fileName)
        tempFile.createNewFile()
        // Initialize streams
        var oStream: FileOutputStream? = null
        var inputStream: InputStream? = null

        try {
            oStream = FileOutputStream(tempFile)
            inputStream = context.contentResolver.openInputStream(contentUri)

            inputStream?.let { copy(inputStream, oStream) }
            oStream.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // Close streams
            inputStream?.close()
            oStream?.close()
        }

        return tempFile
    }

    private fun getFileExtension(context: Context, uri: Uri): String? =
        if (uri.scheme == ContentResolver.SCHEME_CONTENT)
            MimeTypeMap.getSingleton().getExtensionFromMimeType(context.contentResolver.getType(uri))
        else uri.path?.let { MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(it)).toString()) }

    @Throws(IOException::class)
    private fun copy(source: InputStream, target: OutputStream) {
        val buf = ByteArray(8192)
        var length: Int
        while (source.read(buf).also { length = it } > 0) {
            target.write(buf, 0, length)
        }
    }

    companion object{
        const val RequestPermissionCode = 111
    }


}
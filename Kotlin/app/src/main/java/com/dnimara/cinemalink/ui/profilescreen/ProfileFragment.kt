package com.dnimara.cinemalink.ui.profilescreen

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dnimara.cinemalink.R
import com.dnimara.cinemalink.application.CinemaLinkApplication
import com.dnimara.cinemalink.databinding.DialogEditUsernameCustomBinding
import com.dnimara.cinemalink.databinding.DialogFollowCustomBinding
import com.dnimara.cinemalink.databinding.FragmentProfileBinding
import com.dnimara.cinemalink.ui.searchuserscreen.SearchUserAdapter
import com.dnimara.cinemalink.ui.searchuserscreen.SearchUserFragmentDirections
import com.dnimara.cinemalink.ui.searchuserscreen.UserDto
import com.dnimara.cinemalink.ui.userscreen.UserFragmentDirections
import com.dnimara.cinemalink.utils.SimpleDividerItemDecoration
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.processNextEventInCurrentThread
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding
    private lateinit var profileViewModel: ProfileViewModel

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

        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_profile, container, false)

        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        val args = ProfileFragmentArgs.fromBundle(requireArguments())
        profileViewModel.getProfileInfo(args.userId)

        val profileAdapter = ProfileAdapter(profileViewModel)
        binding.rvProfileUserPosts.apply {
            addItemDecoration(SimpleDividerItemDecoration(context, R.drawable.line_divider))
            adapter = profileAdapter
        }

        profileViewModel.profileInfo.observe(viewLifecycleOwner) {
            it?.apply {
                binding.profile = it
                profileAdapter.submitList(it.posts)
            }
            if (binding.loadingProfilePanel.visibility == View.VISIBLE) {
                binding.loadingProfilePanel.visibility = View.GONE
            }
            if (binding.mlProfile.visibility == View.GONE) {
                binding.mlProfile.visibility = View.VISIBLE
            }
        }

        profileViewModel.navigateToSelectedMovie.observe(viewLifecycleOwner) {
            if (it != null) {
                findNavController().navigate(
                    ProfileFragmentDirections.actionProfileFragmentToMovieFragment(
                        it
                    )
                )
                profileViewModel.displayMovieComplete()
            }
        }
        profileViewModel.navigateToPostScreen.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    ProfileFragmentDirections.actionProfileFragmentToPostFragment(it)
                )
                profileViewModel.navigateToPostComplete()
            }
        }
        profileViewModel.navigateToSelectedUserProfile.observe(viewLifecycleOwner) {
            if (it != null) {
                findNavController().navigate(
                    ProfileFragmentDirections
                        .actionProfileFragmentToUserFragment(it))
                profileViewModel.displayUserProfileComplete()
            }
        }
        profileViewModel.showButtons.observe(viewLifecycleOwner) {
            activity?.runOnUiThread {
                if (it) {
                    if (binding.confirmUpdateProfilePicButton.visibility == View.GONE)
                        binding.confirmUpdateProfilePicButton.visibility = View.VISIBLE
                    if (binding.reverseUpdateProfilePicButton.visibility == View.GONE)
                        binding.reverseUpdateProfilePicButton.visibility = View.VISIBLE
                } else {
                    if (binding.confirmUpdateProfilePicButton.visibility == View.VISIBLE)
                        binding.confirmUpdateProfilePicButton.visibility = View.GONE
                    if (binding.reverseUpdateProfilePicButton.visibility == View.VISIBLE)
                        binding.reverseUpdateProfilePicButton.visibility = View.GONE
                }
            }
        }
        profileViewModel.shareContent.observe(viewLifecycleOwner) {
            it?.let {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT, if (it.reference == "") CinemaLinkApplication
                            .mInstance.applicationContext.resources.getString(
                                R.string.share_content_no_tag, it.username, it.content)
                        else CinemaLinkApplication.mInstance.applicationContext.resources.getString(
                            R.string.share_content, it.username, it.content, it.reference))
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
                profileViewModel.shareComplete()
            }
        }

        binding.updateProfilePicButton.setOnClickListener {
            openDialog()
        }
        binding.updateUsernameButton.setOnClickListener {
            displayEditUsernameAlertDialog(profileViewModel.profileInfo.value?.username!!)
        }
        binding.confirmUpdateProfilePicButton.setOnClickListener {
            profileViewModel.updateProfilePic(croppedFile!!)
            profileViewModel.hideButtons()
        }
        binding.reverseUpdateProfilePicButton.setOnClickListener {
            profileViewModel.hideButtons()
            binding.profile = profileViewModel.profileInfo.value
        }
        binding.followerStatProfile.setOnClickListener {
            if (!profileViewModel.profileInfo.value?.followerUsers.isNullOrEmpty()) {
                displayShowFollowDialog(profileViewModel.profileInfo.value?.followerUsers!!)
            }
        }
        binding.followingStatProfile.setOnClickListener {
            if (!profileViewModel.profileInfo.value?.followingUsers.isNullOrEmpty()) {
                displayShowFollowDialog(profileViewModel.profileInfo.value?.followingUsers!!)
            }
        }

        binding.lifecycleOwner = this.viewLifecycleOwner
        return binding.root
    }

    private fun openDialog() {
        val openDialog = AlertDialog.Builder(this.context, R.style.MyDialogTheme)
        openDialog.setIcon(R.drawable.ic_image)
        openDialog.setTitle("Choose your new image from...")
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
        galIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(
            Intent.createChooser(galIntent,
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
        val storageDir: File =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
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
                this.requireActivity(), Manifest.permission.CAMERA
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
        if (requestCode == 0 && resultCode == Activity.RESULT_OK){
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
                binding.ivProfilePicProfile.setImageBitmap(bitmap)
                profileViewModel.showButtons()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            RequestPermissionCode-> if ( grantResults.size > 0
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
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
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

    private fun displayEditUsernameAlertDialog(username: String) {
        val builder = AlertDialog.Builder(activity)
        val dialogEditCustomBinding: DialogEditUsernameCustomBinding =
            DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_edit_username_custom,
                null, false)
        dialogEditCustomBinding.etEditUsernameDialog.setText(username)
        builder.setCancelable(false)
        builder.setView(dialogEditCustomBinding.root)

        val dialog = builder.create()
        dialog.show()

        dialogEditCustomBinding.cancelEditUsernameDialog.setOnClickListener {
            dialog.dismiss()
        }
        dialogEditCustomBinding.buttonEditUsernameDialog.setOnClickListener {
            if (dialogEditCustomBinding.etEditUsernameDialog.text.toString().isBlank()) {
                dialogEditCustomBinding.etEditUsernameDialog.error =
                    "A username content is required!"
            } else if (dialogEditCustomBinding.etEditUsernameDialog.text.toString().length < 4) {
                Toast.makeText(
                    CinemaLinkApplication.mInstance.applicationContext,
                    "The username is too short.", Toast.LENGTH_SHORT
                ).show()
            }
            else if (dialogEditCustomBinding.etEditUsernameDialog.text.toString().length > 36) {
                Toast.makeText(
                    CinemaLinkApplication.mInstance.applicationContext,
                    "The username is too long.", Toast.LENGTH_SHORT
                ).show()
            } else {
                profileViewModel.updateUsername(
                    dialogEditCustomBinding
                        .etEditUsernameDialog.text.toString().trim())
                dialog.dismiss()
                Toast.makeText(
                    CinemaLinkApplication.mInstance.applicationContext,
                    "Username updated.", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun displayShowFollowDialog(users: List<UserDto>) {
        val builder = AlertDialog.Builder(activity)
        lateinit var dialog: AlertDialog
        val dialogBinding: DialogFollowCustomBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
            R.layout.dialog_follow_custom, null, false)
        val userAdapter = SearchUserAdapter(SearchUserAdapter.SearchUserListener {
            profileViewModel.displayUserProfile(it)
            dialog.dismiss()
        })
        dialogBinding.rvFollowDialog.apply {
            addItemDecoration(SimpleDividerItemDecoration(context, R.drawable.line_divider))
            adapter = userAdapter
        }
        userAdapter.submitList(users)
        builder.setView(dialogBinding.root)

        dialog = builder.create()
        dialog.show()
    }

    override fun onStart() {
        super.onStart()

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        activity?.runOnUiThread {
            if (navBar.visibility == View.GONE) {
                navBar.visibility = View.VISIBLE
            }
        }
    }

}
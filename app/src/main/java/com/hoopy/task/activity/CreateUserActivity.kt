package com.hoopy.task.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.hoopy.task.R
import com.hoopy.task.constants.Constant
import com.hoopy.task.databinding.ActivityCreateUserBinding
import android.graphics.Bitmap
import androidx.core.content.ContextCompat
import croom.konekom.`in`.hoopy.rest.ApiClient
import croom.konekom.`in`.hoopy.rest.ApiInterface


import android.net.Uri
import android.util.Log
import com.hoopy.task.constants.closeKeyboard
import com.hoopy.task.constants.isNetWorkAvailable
import com.hoopy.task.remote.response.PhotoUploadResponse
import com.hoopy.task.remote.response.UserUploadResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import java.io.File
import okhttp3.RequestBody

import okhttp3.MediaType
import okhttp3.MultipartBody
import java.io.ByteArrayOutputStream
import java.math.BigInteger


/**
 * Created by kartikeysrivastava on 2019-10-19
 */


class CreateUserActivity : AppCompatActivity(), View.OnClickListener {

    private val IMAGE_CODE = 100
    private lateinit var uiBinding: ActivityCreateUserBinding
    private var fileUri: Uri? = null
    private var currentfilePath = ""
    private lateinit var mContext: Context
    val apiInterface = ApiClient.client.create(ApiInterface::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_user)
        setSupportActionBar(uiBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Create User"
        uiBinding.submitData.setOnClickListener(this)
        uiBinding.uploadImage.setOnClickListener(this)
        mContext = this
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.submitData -> {
                if (this.isNetWorkAvailable())
                    validateAndSubmitData()
                else {
                    Toast.makeText(this, "Network not available", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.uploadImage -> openImageSelector()
        }
    }

    private fun openImageSelector() {

        startDialog()

    }

    private fun startDialog() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);


        val chooser = Intent.createChooser(photoPickerIntent, "Some text here")
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takePictureIntent))
        startActivityForResult(chooser, IMAGE_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let {
                    currentfilePath = ""
                    //If image is picked from gallery
                    if (it.data != null) {
                        var selectedImageURI = it.data
                        currentfilePath = getRealPathFromURI(selectedImageURI!!)
                        uiBinding.uploadImage.setImageURI(selectedImageURI)
                    }
                    //If image is picked from camera
                    else {
                        val thumbnail = data.extras?.get("data") as Bitmap
                        uiBinding.uploadImage.setImageBitmap(thumbnail)
                        currentfilePath = getRealPathFromURI(getImageUri(this, thumbnail))
                    }
                }

            }
        }
    }

    /***
     * If the image is clicked from camera it is first stored in android database and through uri its exact path is found
     */
    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        var bytes = ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        var path = MediaStore.Images.Media.insertImage(
            inContext.getContentResolver(),
            inImage,
            System.currentTimeMillis().toString() + ".jpg",
            null
        );
        return Uri.parse(path)
    }

    /***
     * Return the path of image by quering Android Media Image Database and then returning path
     */
    fun getRealPathFromURI(uri: Uri): String {
        var path = ""
        if (contentResolver != null) {
            val cursor = contentResolver.query(uri, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
    }

    /***
     * Validates if all the data are according to correct format
     */
    private fun validateAndSubmitData() {
        this.closeKeyboard()
        var isContactValid = Constant.isContactValid(uiBinding.contactText.text.toString())

        var isEmailValid = Constant.isEmailIdValid(uiBinding.emailText.text.toString())

        var isUserNameValid = Constant.isUserNameValid(uiBinding.userNameText.text.toString())

        var isNameValid = Constant.isNameValid(uiBinding.nameText.text.toString())

        var isImageValid =
            (uiBinding.uploadImage.getDrawable().constantState != ContextCompat.getDrawable(
                this,
                R.drawable.upload_image
            )?.constantState)

        //  uiBinding.uploadImage.drawable != ResourcesCompat.getDrawable(resources, R.drawable.upload_image, null)
        if (isEmailValid && isContactValid && isUserNameValid && isNameValid && isImageValid) {
            uploadImageToServer()
        }


        if (!isEmailValid)
            uiBinding.emailText.error = "Invalid Email"

        if (!isContactValid)
            uiBinding.contactText.error =
                "Invalid Contact. Mobile number should be of 10 digit and should begin with 7,8 or 9 "

        if (uiBinding.nameText.text.toString().isEmpty() || !isNameValid)
            uiBinding.nameText.error = "Invalid Name. Name should only contain alphabet and spaces."

        if (!isUserNameValid)
            uiBinding.userNameText.error =
                "Invalid UserName. Minimum 4 letter required and can contain only alphabet or digit"

        if (!isImageValid)
            Toast.makeText(this, "Please enter a profile picture too", Toast.LENGTH_SHORT).show()
    }

    /***
     * If data format are correct and image is present then image is sent to server
     */
    private fun uploadImageToServer() {

        val file = File(currentfilePath)
        val fileReqBody = RequestBody.create(MediaType.parse("image/*"), file)
        val part = MultipartBody.Part.createFormData("upload", file.getName(), fileReqBody);
        uiBinding.progressBar.visibility = View.VISIBLE
        apiInterface.uploadPhoto(part).enqueue(object : Callback<PhotoUploadResponse> {
            override fun onResponse(
                call: Call<PhotoUploadResponse>,
                response: Response<PhotoUploadResponse>
            ) {
                Log.d("Api Success ", "Api sucess" + response.body()!!.toString())
                uploadUserDetails(response.body()!!.url.get(0))
                uiBinding.progressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<PhotoUploadResponse>, t: Throwable) {
                uiBinding.progressBar.visibility = View.GONE
                Toast.makeText(mContext, "Error in inserting data", Toast.LENGTH_SHORT).show()

            }
        })
    }

    /***
     * If image is successfully loaded at the server then the details and image url are sent to server for registration
     */
    private fun uploadUserDetails(imageUrl: String?) {
        if (this.isNetWorkAvailable()) {
            uiBinding.progressBar.visibility = View.VISIBLE
            apiInterface.insertUser(
                uiBinding.nameText.text.toString(),
                uiBinding.emailText.text.toString(),
                uiBinding.userNameText.text.toString(),
                uiBinding.contactText.text.toString(),
                imageUrl!!
            ).enqueue(object : Callback<UserUploadResponse> {
                override fun onResponse(
                    call: Call<UserUploadResponse>,
                    response: Response<UserUploadResponse>
                ) {
                    uiBinding.progressBar.visibility = View.GONE
                    Toast.makeText(mContext, "User Registered Successfully", Toast.LENGTH_LONG)
                        .show()
                    Log.d("Api Success ", "Api sucess" + response.body()!!.toString())
                    finish()
                }

                override fun onFailure(call: Call<UserUploadResponse>, t: Throwable) {
                    uiBinding.progressBar.visibility = View.GONE
                    Toast.makeText(mContext, "Error in insertin data", Toast.LENGTH_SHORT)
                        .show()

                }
            })
        } else {
            Toast.makeText(this, "Network not available", Toast.LENGTH_SHORT).show()
        }
    }
}
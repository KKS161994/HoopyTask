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
        title = "Create User"
        uiBinding.submitData.setOnClickListener(this)
        uiBinding.uploadImage.setOnClickListener(this)
        mContext = this
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.submitData -> validateAndSubmitData()
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
                    if (it.data != null) {
                        var selectedImageURI = it.data
                        currentfilePath = getRealPathFromURI(selectedImageURI!!)
                        uiBinding.uploadImage.setImageURI(selectedImageURI)
                    } else {
                        val thumbnail = data.extras?.get("data") as Bitmap
                        uiBinding.uploadImage.setImageBitmap(thumbnail)
                        currentfilePath = getRealPathFromURI(getImageUri(this,thumbnail))
                    }
//                    Toast.makeText(
//                        this,
//                        "Please enter a profile picture too   " + currentfilePath,
//                        Toast.LENGTH_SHORT
//                    ).show()
                }

            }
        }
    }



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

    private fun validateAndSubmitData() {
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
            Toast.makeText(this, "Data Valid   $isImageValid", Toast.LENGTH_SHORT).show()
            uploadImageToServer()
        }


        if (!isEmailValid)
            uiBinding.emailText.error = "Invalid Email"
        if (!isContactValid)
            uiBinding.contactText.error = "Invalid Contact"
        if (uiBinding.nameText.text.toString().isEmpty() || !isNameValid)
            uiBinding.nameText.error = "Invalid Name"
        if (!isUserNameValid)
            uiBinding.userNameText.error = "Invalid UserName"

        if (!isImageValid)
            Toast.makeText(this, "Please enter a profile picture too", Toast.LENGTH_SHORT).show()
    }

    private fun uploadImageToServer() {

        val file = File(currentfilePath)
        val fileReqBody = RequestBody.create(MediaType.parse("image/*"), file)
        val part = MultipartBody.Part.createFormData("upload", file.getName(), fileReqBody);

        apiInterface.uploadPhoto(part).enqueue(object : Callback<PhotoUploadResponse> {
            override fun onResponse(
                call: Call<PhotoUploadResponse>,
                response: Response<PhotoUploadResponse>
            ) {
                //Toast.makeText(mContext,"Succes api "+response.body()?.url.toString(),Toast.LENGTH_SHORT).show()
                Log.d("Api Success ", "Api sucess" + response.body()!!.toString())
                uploadUserDetails(response.body()!!.url.get(0))
            }

            override fun onFailure(call: Call<PhotoUploadResponse>, t: Throwable) {
                Toast.makeText(mContext, "Failure api $t", Toast.LENGTH_SHORT).show()

            }
        })
    }

    private fun uploadUserDetails(imageUrl: String?) {
        apiInterface.insertUser(uiBinding.nameText.text.toString(),
                uiBinding.emailText.text.toString(),uiBinding.userNameText.text.toString(),uiBinding.contactText.text.toString(),
                imageUrl!!
            ).enqueue(object : Callback<UserUploadResponse> {
            override fun onResponse(
                call: Call<UserUploadResponse>,
                response: Response<UserUploadResponse>
            ) {
                Toast.makeText(mContext,"User Registered Successfully",Toast.LENGTH_LONG).show()
                Log.d("Api Success ", "Api sucess" + response.body()!!.toString())
                finish()
            }

            override fun onFailure(call: Call<UserUploadResponse>, t: Throwable) {
                Toast.makeText(mContext, "Failure api insert user $t", Toast.LENGTH_SHORT).show()

            }
        })
    }
}
package com.hoopy.task.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.hoopy.task.R
import com.hoopy.task.constants.Constant
import com.hoopy.task.constants.closeKeyboard
import com.hoopy.task.databinding.ActivityCreateUserBinding
import com.hoopy.task.remote.response.UserFetchResponse
import croom.konekom.`in`.hoopy.rest.ApiClient
import croom.konekom.`in`.hoopy.rest.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.app.Activity
import android.content.Intent
import com.hoopy.task.constants.isNetWorkAvailable


/**
 * Created by kartikeysrivastava on 2019-10-20
 */

class UpdateUserActivity : AppCompatActivity(), View.OnClickListener {


    private lateinit var uiBinding: ActivityCreateUserBinding
    private var id = -1
    private lateinit var context: Context
    val apiInterface = ApiClient.client.create(ApiInterface::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_user)
        setSupportActionBar(uiBinding.toolbar)
        title = "Update User details"

        context = this
        uiBinding.nameText.setText(intent.getStringExtra("Name"))
        uiBinding.contactText.setText(intent.getStringExtra("Contact"))
        uiBinding.emailText.setText(intent.getStringExtra("Email"))
        uiBinding.userNameText.setText(intent.getStringExtra("Username"))
        id = intent.getIntExtra("Id", -1)
        Glide.with(this)
            .load(intent.getStringExtra("ImageUrl"))
            .into(uiBinding.uploadImage)
        uiBinding.submitData.text = "Update Data"
        uiBinding.uploadImage.setOnClickListener(this)
        uiBinding.submitData.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.submitData -> {
                    if(this.isNetWorkAvailable())
                    validateAndUpdateData()
                    else{
                        Toast.makeText(this,"Network not available",Toast.LENGTH_SHORT).show()
                    }
                }
                R.id.uploadImage -> {
                    Toast.makeText(context, "Image Update still not available", Toast.LENGTH_SHORT)
                        .show()
                }
                else -> {
                }
            }

        }
    }

    private fun validateAndUpdateData() {
        this.closeKeyboard()
        var isContactValid = Constant.isContactValid(uiBinding.contactText.text.toString())

        var isEmailValid = Constant.isEmailIdValid(uiBinding.emailText.text.toString())

        var isUserNameValid = Constant.isUserNameValid(uiBinding.userNameText.text.toString())

        var isNameValid = Constant.isNameValid(uiBinding.nameText.text.toString())
        if (isEmailValid && isContactValid && isUserNameValid && isNameValid) {

            uploadDataToServer()
        }
        if (!isEmailValid)
            uiBinding.emailText.error = "Invalid Email"

        if (!isContactValid)
            uiBinding.contactText.error =
                "Invalid Contact. Mobile number should be of 10 digit and should begin with 7,8 or 9 "

        if (uiBinding.nameText.text.toString().isEmpty() || !isNameValid)
            uiBinding.nameText.error = "Invalid Name"

        if (!isUserNameValid)
            uiBinding.userNameText.error =
                "Invalid UserName. Minimum 4 letter required and can contain only alphabet or digit"

    }

    private fun uploadDataToServer() {
        uiBinding.progressBar.visibility = View.VISIBLE
        apiInterface.updateUser(
            id,
            uiBinding.nameText.text.toString(),
            uiBinding.emailText.text.toString(),
            uiBinding.userNameText.text.toString(),
            uiBinding.contactText.text.toString()
        ).enqueue(object : Callback<UserFetchResponse> {
            override fun onResponse(
                call: Call<UserFetchResponse>,
                response: Response<UserFetchResponse>
            ) {
                uiBinding.progressBar.visibility = View.GONE
                Toast.makeText(context, "User Updated Successfully", Toast.LENGTH_LONG).show()
                Log.d("Api Success ", "Api sucess" + response.body()!!.toString())
                val returnIntent = Intent()
                returnIntent.putExtra("Name", uiBinding.nameText.text.toString())
                returnIntent.putExtra("UserName", uiBinding.userNameText.text.toString())
                returnIntent.putExtra("Contact", uiBinding.contactText.text.toString())
                returnIntent.putExtra("Email", uiBinding.emailText.text.toString())
                returnIntent.putExtra("position", intent.getIntExtra("position", -1))
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }

            override fun onFailure(call: Call<UserFetchResponse>, t: Throwable) {
                uiBinding.progressBar.visibility = View.GONE
                Toast.makeText(context, "Error updating data", Toast.LENGTH_SHORT).show()

            }
        })
    }
}
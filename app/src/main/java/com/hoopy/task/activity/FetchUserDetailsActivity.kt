package com.hoopy.task.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.hoopy.task.R
import com.hoopy.task.constants.Constant
import com.hoopy.task.databinding.ActivityFetchUserDetailsBinding
import com.hoopy.task.remote.response.UserFetchResponse
import croom.konekom.`in`.hoopy.rest.ApiClient
import croom.konekom.`in`.hoopy.rest.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.hoopy.task.adapter.UsersListAdapter
import com.hoopy.task.constants.closeKeyboard
import com.hoopy.task.constants.isNetWorkAvailable
import com.hoopy.task.remote.response.User
import java.text.FieldPosition


/**
 * Created by kartikeysrivastava on 2019-10-20
 */

class FetchUserDetailsActivity : AppCompatActivity(), View.OnClickListener {


    private lateinit var userListAdapter: UsersListAdapter
    private lateinit var uiBinding: ActivityFetchUserDetailsBinding
    val apiInterface = ApiClient.client.create(ApiInterface::class.java)
    private lateinit var userFetchResponse: Call<UserFetchResponse>
    private lateinit var mContext: Context
    private var listOfUser = ArrayList<User>()
    private var UPDATE_CODE = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiBinding = DataBindingUtil.setContentView(this, R.layout.activity_fetch_user_details)
        mContext = this
        setSupportActionBar(uiBinding.toolbar)
        title = "Get User"
        uiBinding.submitData.setOnClickListener(this)
        var filterItemArray = resources.getStringArray(R.array.array_name)
        val spinneradapter = ArrayAdapter<String>(this, R.layout.spinner_item, filterItemArray)
        uiBinding.filterSpinner.adapter = spinneradapter
        setAdapter()
    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.submitData -> validateAndFetchUserDetails()
            }
        }
    }

    fun onItemClick(position: Int) {
        val user = listOfUser[position]
        val intent = Intent(this, UpdateUserActivity::class.java)
        intent.putExtra("Name", user.name)
        intent.putExtra("Email", user.email)
        intent.putExtra("Contact", user.contact)
        intent.putExtra("Username", user.username)
        intent.putExtra("ImageUrl", user.image_url)
        intent.putExtra("Id", user.id)
        intent.putExtra("position", position)
        startActivityForResult(intent, UPDATE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPDATE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let { data ->
                    val name = data.getStringExtra("Name")
                    val email = data.getStringExtra("Email")
                    val username = data.getStringExtra("UserName")
                    val contact = data.getStringExtra("Contact")
                    val position = data.getIntExtra("position", -1)
                    val user = listOfUser[position]
                    user.name = name
                    user.email = email
                    user.contact = contact
                    user.username = username
                    listOfUser.set(position, user)
                    userListAdapter.notifyItemChanged(position)
                }
            }
        }
    }

    private fun validateAndFetchUserDetails() {
        this.closeKeyboard()
        val selectedItem = uiBinding.filterSpinner.selectedItem.toString()
        val detailText = uiBinding.filterText.text.toString()
        when (selectedItem) {
            "Name" -> {
                if (Constant.isNameValid(detailText) && uiBinding.filterText.text.isNotEmpty()) {
                    userFetchResponse = apiInterface.fetchUser(detailText, null, null, null)
                    getData()
                } else {
                    uiBinding.filterText.error = "Invalid Name"
                }
            }
            "Email" -> {
                if (Constant.isEmailIdValid(detailText)) {
                    userFetchResponse = apiInterface.fetchUser(null, detailText, null, null)
                    getData()
                } else {
                    uiBinding.filterText.error = "Invalid Email"
                }
            }
            "Username" -> {
                if (Constant.isUserNameValid(detailText)) {
                    userFetchResponse = apiInterface.fetchUser(null, null, detailText, null)
                    getData()
                } else {
                    uiBinding.filterText.error = "Invalid Username"
                }
            }
            "Contact" -> {
                if (Constant.isContactValid(detailText)) {
                    userFetchResponse = apiInterface.fetchUser(null, null, null, detailText)
                    getData()
                } else {
                    uiBinding.filterText.error = "Invalid Contact"
                }

            }
        }


    }

    private fun getData() {
        if (mContext.isNetWorkAvailable()) {
            uiBinding.progressBar.visibility = View.VISIBLE
            userFetchResponse.enqueue(object : Callback<UserFetchResponse> {
                override fun onFailure(call: Call<UserFetchResponse>, t: Throwable) {
                    Toast.makeText(mContext, "Error in retreiving data", Toast.LENGTH_LONG).show()
                    Log.d("Api Failure", "Api failure $t")
                    uiBinding.progressBar.visibility = View.GONE
                }

                override fun onResponse(
                    call: Call<UserFetchResponse>,
                    response: Response<UserFetchResponse>
                ) {
                    Toast.makeText(mContext, "Success", Toast.LENGTH_LONG).show()
                    Log.d("Api Success", "Api Success " + response.body().toString())

                    if (response.body()?.usersList != null) {
                        listOfUser.clear()
                        listOfUser.addAll(response.body()!!.usersList)
                        userListAdapter.users = listOfUser
                        uiBinding.progressBar.visibility = View.GONE
                    }
                }
            })
        } else {
            Toast.makeText(this, "Network not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setAdapter() {
        userListAdapter = UsersListAdapter(this)
        uiBinding.usersList.adapter = userListAdapter
        uiBinding.usersList.layoutManager = LinearLayoutManager(this)
    }
}

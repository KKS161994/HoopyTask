package com.hoopy.task.remote.response

import com.google.gson.annotations.SerializedName

/**
 * Created by kartikeysrivastava on 2019-10-20
 */
data class User(@field:SerializedName("id") var id: Int,
                @field:SerializedName("name") var name: String,
                @field:SerializedName("email") var email: String,
                @field:SerializedName("contact") var contact: String,
                @field:SerializedName("username") var username: String,
                @field:SerializedName("image_url") var image_url: String

)
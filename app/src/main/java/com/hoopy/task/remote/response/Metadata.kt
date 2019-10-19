package com.hoopy.task.remote.response

import com.google.gson.annotations.SerializedName

/**
 * Created by kartikeysrivastava on 2019-10-20
 */
data class MetaData(@field:SerializedName("response_code")
                    var responseCode: Int, @field:SerializedName("response_text")
                    var responseText: String)
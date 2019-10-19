package com.hoopy.task.remote.response

import com.google.gson.annotations.SerializedName

/**
 * Created by kartikeysrivastava on 2019-10-20
 */

data class PhotoUploadResponse(@field:SerializedName("urls")
                           var url: ArrayList<String>, @field:SerializedName("metadata")
                           var metaDataa: MetaData)



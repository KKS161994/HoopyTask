package com.hoopy.task.remote.response

import com.google.gson.annotations.SerializedName

/**
 * Created by kartikeysrivastava on 2019-10-20
 */
data class UserFetchResponse(@field:SerializedName("data")
                              var usersList: List<User>, @field:SerializedName("metadata")
                              var metaDataa: MetaData)


package croom.konekom.`in`.hoopy.rest

import com.hoopy.task.constants.Constant
import com.hoopy.task.remote.response.PhotoUploadResponse
import com.hoopy.task.remote.response.UserUploadResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*
import java.math.BigInteger

/***
 * Created By Kartikey Kumar Srivastava
 */

//API to be called
interface ApiInterface {
    @Multipart
    @POST(Constant.FILE_UPLOAD_URL)
    fun uploadPhoto(@Part model: MultipartBody.Part): Call<PhotoUploadResponse>
    @FormUrlEncoded
    @POST(Constant.INSERT_USER_URL)
    fun insertUser(@Field("name") name:String,
                   @Field("email") email:String,
                   @Field("username") username:String,
                   @Field("contact") contact: String,
                   @Field("image_url") image_url:String):Call<UserUploadResponse>

}

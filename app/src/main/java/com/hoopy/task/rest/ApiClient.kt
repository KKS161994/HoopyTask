package croom.konekom.`in`.hoopy.rest

import com.google.gson.GsonBuilder
import com.hoopy.task.constants.Constant

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/***
 * Created By Kartikey Kumar Srivastava
 */


object ApiClient {
    private var retrofit: Retrofit? = null
    internal var gson = GsonBuilder()
            .setLenient()
            .create()

    val client: Retrofit
        get() {
            if (retrofit == null) {
                retrofit = retrofit2.Retrofit.Builder()
                        .baseUrl(Constant.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build()
            }
            return retrofit!!
        }

}

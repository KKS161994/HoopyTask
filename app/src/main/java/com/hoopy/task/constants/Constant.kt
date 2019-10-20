package com.hoopy.task.constants

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import com.hoopy.task.ConnectivityController
import java.util.regex.Pattern

/**
 * Created by kartikeysrivastava on 2019-10-19
 */

object Constant {
    const val BASE_URL = "https://www.team.hoopy.in/api/1.0/testApis/"
    const val FILE_UPLOAD_URL = "upload_test"
    const val INSERT_USER_URL = "insert_test"
    const val FETCH_USER_URL = "fetch_data_test"
    const val UPDATE_USER_URL = "update_data_test"
    fun isEmailIdValid(email: String): Boolean {

            return Pattern.compile(
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
            ).matcher(email).matches()
        }

        fun isNameValid(txt: String): Boolean {

            return Pattern.compile("^[a-zA-Z\\s]*$").matcher(txt).matches()

        }

        fun isUserNameValid(txt: String): Boolean {
            return Pattern.compile("^[a-z0-9_-]{3,15}$").matcher(txt).matches()
        }

        fun isContactValid(txt: String): Boolean {
            return Pattern.compile("^[7-9][0-9]{9}$").matcher(txt).matches()
        }


}
fun Activity.closeKeyboard()  {
    val inputManager = this?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val focusedView = this.currentFocus
    if (focusedView != null) {
        inputManager.hideSoftInputFromWindow(focusedView.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS)
    }
}
fun Context.isNetWorkAvailable(): Boolean {
    return ConnectivityController.isNetworkAvailable(this)
}


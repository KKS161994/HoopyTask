package com.hoopy.task.activity

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.app.ActivityCompat
import com.hoopy.task.R
import com.hoopy.task.util.PermissionUtil

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class SplashActivity : AppCompatActivity(), PermissionUtil.ShowAlertCallback {


    private val permission = Manifest.permission.CAMERA
    private val permissionUtil by  lazy{PermissionUtil(this,this)}
    private var alertDialog:AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)


       // permissionUtil.checkorRequestPermission(permission)

    }


    override fun onStart() {
        super.onStart()
        if(alertDialog==null||!(alertDialog!!.isShowing)){
            permissionUtil.checkorRequestPermission(permission)
        }
    }
    override fun showAlert() {
        var alertDialogbilder = AlertDialog.Builder(ContextThemeWrapper(this, android.R.style.Theme_Light_NoTitleBar))
        alertDialogbilder?.setTitle("Permission")

        alertDialogbilder?.setMessage("Camera permission is required for the App to continue.")
            ?.setCancelable(false)
            ?.setNeutralButton("Ok") { dialog, id ->

                alertDialog?.cancel()
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    100
                )
            }
         alertDialog = alertDialogbilder?.create()
        if (alertDialog == null || !(alertDialog!!.isShowing))
            alertDialog?.show()
         //To change body of created functions use File | Settings | File Templates.
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //Say request permission has been activated . It will come to onRequestPermissionsResult after allowing or denying permission
        //So permission request must be set tp false in order to prohibit request of permission check again
        if(requestCode == 100){
            permissionUtil.checkorRequestPermission(permission)

        }
    }

    override fun permissionGranted() {
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1000)
    }


}

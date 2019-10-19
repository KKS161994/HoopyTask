package com.hoopy.task.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.hoopy.task.R
import com.hoopy.task.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(),View.OnClickListener {


    private lateinit var uiBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(uiBinding.toolbar)
        uiBinding.toolbar.title = "Hoopy"
        uiBinding.insertIcon.setOnClickListener(this)
        uiBinding.insertText.setOnClickListener(this)
        uiBinding.updateIcon.setOnClickListener(this)
        uiBinding.updateText.setOnClickListener(this)

    }
    override fun onClick(v: View?) {
        v?.let {
            when(v.id){
                R.id.insertIcon, R.id.insertText -> launchInsertActivity()
                R.id.updateIcon, R.id.updateText -> launchUpdateActivity()
            }

        }

    }

    private fun launchUpdateActivity() {
    }

    private fun launchInsertActivity() {
        startActivity(Intent(this, CreateUserActivity::class.java))

    }
}

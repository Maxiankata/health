package com.example.healthtracker

import android.app.job.JobInfo.PRIORITY_DEFAULT
import android.content.Intent
import android.content.RestrictionsManager.RESULT_ERROR
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Priority
import com.example.healthtracker.databinding.ActivityCropBinding
import com.example.healthtracker.databinding.ActivityMainBinding
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.UUID

class CropActivity : AppCompatActivity(), OnBackPressedDispatcherOwner {
    lateinit var uri: Uri
    lateinit var result: String
    lateinit var  binding : ActivityCropBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readIntent()
        binding = ActivityCropBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val destinationUri: String =
            StringBuilder(UUID.randomUUID().toString()).append(".png").toString()
        val options = UCrop.Options()
        options.setCircleDimmedLayer(true)
        options.setFreeStyleCropEnabled(false)
        UCrop.of(uri, Uri.fromFile(File(cacheDir, destinationUri)))
            .withOptions(options)
            .withAspectRatio(1F, 1F)
            .withMaxResultSize(600, 600)
            .start(this@CropActivity)

    }
    private fun readIntent() {
        if (intent.extras != null) {
            result = intent.getStringExtra("DATA")!!
            uri = Uri.parse(result)

        }
    }
    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== RESULT_OK && requestCode==UCrop.REQUEST_CROP){
            val uri = data?.let { UCrop.getOutput(it) }
            val bonusIntent:Intent = intent
            bonusIntent.putExtra("RESULT", uri.toString())
            setResult(RESULT_OK ,bonusIntent)
        }else if(resultCode== RESULT_ERROR){
            val error = data?.let { UCrop.getError(it) }
            error?.let { throw it }
        }
        finish()
    }

}
package com.example.healthtracker

import android.content.Intent
import android.content.RestrictionsManager.RESULT_ERROR
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.yalantis.ucrop.UCrop
import java.io.File
import java.lang.StringBuilder
import java.util.UUID

class CropActivity : AppCompatActivity() {
    lateinit var uri: Uri
    lateinit var result: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)
        readIntent()
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
            setResult(-1,bonusIntent)
            finish()
        }else if(resultCode== RESULT_ERROR){
            val error = data?.let { UCrop.getError(it) }
            Log.d("ERROR", error.toString())
            error?.let { throw it }
        }
    }

}
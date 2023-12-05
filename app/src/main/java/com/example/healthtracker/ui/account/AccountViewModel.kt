package com.example.healthtracker.ui.account

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AccountViewModel : ViewModel() {

    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> = _imageUri

    private val _requestImageSelection = MutableLiveData<Unit>()
    val requestImageSelection: LiveData<Unit> = _requestImageSelection

    fun onImageSelectionRequested() {
        _requestImageSelection.value = Unit
    }

    fun setImageUri(uri: Uri?) {
        _imageUri.value = uri
    }

    fun handleImageSelectionResult(data: Intent?) {
        val selectedImageUri: Uri? = data?.data
        setImageUri(selectedImageUri)
    }
    fun openImagePicker(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        launcher.launch(intent)
    }
}
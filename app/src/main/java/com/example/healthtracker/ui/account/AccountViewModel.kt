package com.example.healthtracker.ui.account

import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.ui.bitmapToBase64
import com.example.healthtracker.ui.saveBitmapToDatabase
import com.example.healthtracker.ui.uriToBitmap
import kotlinx.coroutines.launch

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

    //FIXME try not to pass android classes to your VMs
    fun handleImageSelectionResult(data: Intent?) {
        val selectedImageUri: Uri? = data?.data
        setImageUri(selectedImageUri)
    }

    //FIXME try not to pass android classes to your VMs
    fun openImagePicker(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        launcher.launch(intent)
    }


}
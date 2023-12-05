package com.example.healthtracker.ui.account

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.healthtracker.FirebaseViewModel
import com.example.healthtracker.databinding.FragmentAccountBinding
import com.example.healthtracker.ui.login.LoginActivity
import com.example.healthtracker.ui.navigateToActivity

import com.example.healthtracker.ui.setRoundedCorners
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private lateinit var auth : FirebaseAuth
    val firebaseViewModel:FirebaseViewModel by activityViewModels()
    private val binding get() = _binding!!

    private lateinit var imageSelectionLauncher: ActivityResultLauncher<Intent>
    private lateinit var accountViewModel: AccountViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        accountViewModel = ViewModelProvider(this).get(AccountViewModel::class.java)
//        imageSelectionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            accountViewModel.handleImageSelectionResult(result.data)
//        }
//        profilePhoto.setOnClickListener {
//            accountViewModel.onImageSelectionRequested()
//        }
//        accountViewModel.apply {
//            requestImageSelection.observe(viewLifecycleOwner) { request ->
//                openImagePicker(imageSelectionLauncher)
//            }
//            imageUri.observe(this){
//                    selectedImageUri->profilePhoto.setImageURI(selectedImageUri)
//            }
//        }
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        val root: View = binding.root
        auth = FirebaseAuth.getInstance()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            binding.profilePhoto.setImageURI(uri)
        }
        lateinit var user:String
            Firebase.database.getReference("user/${auth.currentUser!!.uid}/userInfo/username").get().addOnCompleteListener {
            user = it.result.getValue(String::class.java).toString()
                binding.username.text = user
        }
        binding.apply {

            signOutButton.apply {
                setRoundedCorners(25F)
                setOnClickListener {
                    navigateToActivity(requireActivity(), LoginActivity::class.java)
                    firebaseViewModel.signout()
                }
            }
            achievements.apply {
                setRoundedCorners(25F)
            }
            friends.apply {
                setRoundedCorners(25F)
            }

            profilePhoto.apply {
                setRoundedCorners(360F)
                setOnClickListener {
                    pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))

                }
            }
            statistics.apply{
//                setRoundedCorners(25F)

            }


        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
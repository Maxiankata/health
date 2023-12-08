package com.example.healthtracker.ui.account

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthtracker.FirebaseViewModel
import com.example.healthtracker.R
import com.example.healthtracker.databinding.FragmentAccountBinding
import com.example.healthtracker.ui.base64ToBitmap
import com.example.healthtracker.ui.bitmapToBase64
import com.example.healthtracker.ui.friends.FriendListAdapter
import com.example.healthtracker.ui.friends.FriendsDialogFragment
import com.example.healthtracker.ui.login.LoginActivity
import com.example.healthtracker.ui.navigateToActivity
import com.example.healthtracker.ui.saveBitmapToDatabase
import com.example.healthtracker.ui.uriToBitmap
import com.example.healthtracker.ui.setRoundedCorners
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private lateinit var auth: FirebaseAuth
    val firebaseViewModel: FirebaseViewModel by activityViewModels()
    private val binding get() = _binding!!
    private lateinit var accountViewModel: AccountViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        accountViewModel = ViewModelProvider(this)[AccountViewModel::class.java]
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        val root: View = binding.root
        auth = FirebaseAuth.getInstance()

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val contentResolver = requireContext().contentResolver
        var userPhoto: String?
        val dialog = FriendsDialogFragment()
        Firebase.database.getReference("user/${auth.currentUser!!.uid}/userInfo/image").get()
            .addOnCompleteListener {
                userPhoto = it.result.getValue(String::class.java)?.toString()
                if (userPhoto != "") {
                    binding.profilePhoto.setImageBitmap(userPhoto?.let { it1 -> base64ToBitmap(it1) })
                }
            }
        val pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
            binding.profilePhoto.setImageURI(uri)
            lifecycleScope.launch {
                val bitmap = uri?.let { uriToBitmap(contentResolver, it) }
                if (bitmap != null) {
                    bitmapToBase64(bitmap)
                    saveBitmapToDatabase(bitmap)
                }
            }

        }

        lateinit var userName: String
        Firebase.database.getReference("user/${auth.currentUser!!.uid}/userInfo/username").get()
            .addOnCompleteListener {
                userName = it.result.getValue(String::class.java).toString()
                binding.username.text = userName
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
                setOnClickListener {
                    dialog.show(requireActivity().supportFragmentManager, "friends dialog")
                }
            }
            profilePhoto.apply {
                setRoundedCorners(360F)
                setOnClickListener {
                    pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                }
            }
            statistics.apply {
                setRoundedCorners(25F)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

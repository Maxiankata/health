package com.example.healthtracker.ui.account
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.healthtracker.CropActivity
import com.example.healthtracker.FirebaseViewModel
import com.example.healthtracker.databinding.FragmentAccountBinding
import com.example.healthtracker.ui.base64ToBitmap
import com.example.healthtracker.ui.friends.FriendListViewModel
import com.example.healthtracker.ui.friends.FriendsDialogFragment
import com.example.healthtracker.ui.login.LoginActivity
import com.example.healthtracker.ui.navigateToActivity
import com.example.healthtracker.ui.saveBitmapToDatabase
import com.example.healthtracker.ui.uriToBitmap
import com.example.healthtracker.ui.setRoundedCorners
import com.example.healthtracker.ui.showBottomNav
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
    val friendListViewModel:FriendListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().showBottomNav()
        accountViewModel = ViewModelProvider(this)[AccountViewModel::class.java]
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        friendListViewModel.fetchAllUsersInfo()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var userPhoto: String?
        val dialog = FriendsDialogFragment()
        Firebase.database.getReference("user/${auth.currentUser!!.uid}/userInfo/image").get()
            .addOnCompleteListener {
                userPhoto = it.result.getValue(String::class.java)?.toString()
                if (userPhoto != "") {
                    binding.profilePhoto.setImageBitmap(userPhoto?.let { it1 -> base64ToBitmap(it1) })
                }
            }

        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            fun onActivityResult(result: Uri) {
                val intent = Intent(context, CropActivity::class.java)
                intent.putExtra("DATA", result.toString())
                startActivityForResult(intent, 101)
            }
            uri?.let { onActivityResult(it) }
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
                    pickImage.launch("image/*")

                }
            }
            statistics.apply {
                setRoundedCorners(25F)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val contentResolver = requireContext().contentResolver

        if (resultCode == -1 && requestCode == 101) {
            val result: String? = data?.getStringExtra("RESULT")
            val resultUri: Uri? = Uri.parse(result)
            if (resultUri != null && result != "") {
                binding.profilePhoto.setImageURI(resultUri)
                lifecycleScope.launch {
                    uriToBitmap(contentResolver, resultUri)?.let { saveBitmapToDatabase(it) }

                }
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

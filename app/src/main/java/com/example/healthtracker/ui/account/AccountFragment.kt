package com.example.healthtracker.ui.account

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.healthtracker.CropActivity
import com.example.healthtracker.R
import com.example.healthtracker.data.user.UserMegaInfo
import com.example.healthtracker.databinding.FragmentAccountBinding
import com.example.healthtracker.ui.account.friends.popup.FriendsDialogFragment
import com.example.healthtracker.ui.base64ToBitmap
import com.example.healthtracker.ui.login.LoginActivity
import com.example.healthtracker.ui.navigateToActivity
import com.example.healthtracker.ui.showBottomNav
import com.example.healthtracker.ui.showLoading
import com.example.healthtracker.ui.showMainLoading
import com.example.healthtracker.ui.uriToBitmap
import kotlinx.coroutines.launch
import kotlin.math.log

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val accountViewModel: AccountViewModel by viewModels()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        requireActivity().showBottomNav()
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dialog = FriendsDialogFragment()
        lifecycleScope.launch {
            val user = accountViewModel.getWholeUser()
            Log.d("USER TAKEN", user?.userInfo.toString())
            val photo = user?.userInfo?.image
            if (photo != null && photo != "") {
                binding.profilePhoto.setImageBitmap(base64ToBitmap(photo))
                binding.profilePhoto.setBackgroundResource(R.drawable.circle_background)

            } else {
                binding.profilePhoto.setImageResource(R.drawable.profile_photo_placeholder)
                binding.profilePhoto.setBackgroundResource(R.drawable.circle_background)

            }
            val userName = user?.userInfo?.username
            binding.username.text = userName
        }

        val pickImage = registerForActivityResult(StartActivityForResult()) { result ->
            fun onActivityResult(data: Intent?) {
                data?.data?.let { uri ->
                    val intent = Intent(context, CropActivity::class.java)
                    intent.putExtra("DATA", uri.toString())
                    startActivityForResult(intent, 101)
                }
            }
            onActivityResult(result.data)
        }

        binding.apply {
            settings.setOnClickListener {
                findNavController().navigate(
                    R.id.action_navigation_notifications_to_settingsFragment
                )
            }
            signOutButton.setOnClickListener {
                navigateToActivity(requireActivity(), LoginActivity::class.java)
                lifecycleScope.launch {
                    accountViewModel.signOut()
                }
            }
            friends.apply {

            }
            friends.setOnClickListener {
                dialog.show(requireActivity().supportFragmentManager, "friends dialog")
            }
            profilePhoto.apply {
                setBackgroundResource(R.drawable.circle_background)
                setOnClickListener {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "image/*"
                    pickImage.launch(intent)

                }
            }
//            backgroundColor.apply {
//                val bgcolor = UserMegaInfo.currentUser.value?.userInfo?.theme
//                Log.d("BGCOLOR", bgcolor.toString())
//                if (bgcolor != null&& bgcolor!="") {
//                    setBackgroundColor(Color.parseColor(bgcolor))
//                }
//            }
//            backgroundFrame.apply {
//
//            }
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
                    uriToBitmap(contentResolver, resultUri)?.let {
                        accountViewModel.saveBitmapToDatabase(it)
                    }
                }
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

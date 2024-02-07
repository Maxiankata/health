package com.example.healthtracker.ui.account

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.healthtracker.CropActivity
import com.example.healthtracker.MainActivity
import com.example.healthtracker.R
import com.example.healthtracker.data.user.UserMegaInfo
import com.example.healthtracker.databinding.FragmentAccountBinding
import com.example.healthtracker.ui.account.friends.popup.FriendsDialogFragment
import com.example.healthtracker.ui.base64ToBitmap
import com.example.healthtracker.ui.login.LoginActivity
import com.example.healthtracker.ui.navigateToActivity
import com.example.healthtracker.ui.setRoundedCorners
import com.example.healthtracker.ui.showBottomNav
import com.example.healthtracker.ui.uriToBitmap
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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
        viewLifecycleOwner.lifecycleScope.launch {
            val user = accountViewModel.getWholeUser()

            val photo = user?.userInfo?.image
            if (photo != null && photo != "") {
                binding.profilePhoto.setImageBitmap(base64ToBitmap(photo))
            } else {
                binding.profilePhoto.setImageResource(R.drawable.profile_photo_placeholder)
            }
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

        viewLifecycleOwner.lifecycleScope.launch {
            val user = accountViewModel.getWholeUser()
            val userName = user?.userInfo?.username
            binding.username.text = userName
        }

        binding.apply {
            signOutButton.apply {
                setRoundedCorners(25F)
                setOnClickListener {
                    navigateToActivity(requireActivity(), LoginActivity::class.java)
                    lifecycleScope.launch {
                        accountViewModel.signOut()
                    }
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
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "image/*"
                    pickImage.launch(intent)
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
                    uriToBitmap(contentResolver, resultUri)?.let {
                        context?.let { it1 -> accountViewModel.saveBitmapToDatabase(it, it1) }
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

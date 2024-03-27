package com.example.healthtracker.ui.account

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.healthtracker.CropActivity
import com.example.healthtracker.MyApplication
import com.example.healthtracker.R
import com.example.healthtracker.databinding.FragmentAccountBinding
import com.example.healthtracker.ui.account.friends.challenges.ChallengeDisplayDialog
import com.example.healthtracker.ui.account.friends.popup.FriendsDialogFragment
import com.example.healthtracker.ui.base64ToBitmap
import com.example.healthtracker.ui.isInternetAvailable
import com.example.healthtracker.ui.login.LoginActivity
import com.example.healthtracker.ui.navigateToActivity
import com.example.healthtracker.ui.showBottomNav
import com.example.healthtracker.ui.uriToBitmap
import kotlinx.coroutines.launch

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
            val photo = user?.userInfo?.image
            if (photo.isNullOrBlank()) {
                binding.profilePhoto.setImageResource(R.drawable.profile_photo_placeholder)
                binding.profilePhoto.setBackgroundResource(R.drawable.circle_background)
            } else {
                binding.profilePhoto.setImageBitmap(base64ToBitmap(photo))
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
            statistics.setOnClickListener {
                lifecycleScope.launch {
                    accountViewModel.sync()

                }
            }
            signOutButton.setOnClickListener {
                    accountViewModel.signOut()
                    navigateToActivity(requireActivity(), LoginActivity::class.java)
            }
            friends.setOnClickListener {
                dialog.show(requireActivity().supportFragmentManager, "friends dialog")
            }
            challengeDialogOpener.setOnClickListener {
                val otherDialog = ChallengeDisplayDialog()
                if (isInternetAvailable(MyApplication.getContext())){
                    otherDialog.show(requireActivity().supportFragmentManager, "friends dialog")
                }else{
                    Toast.makeText(MyApplication.getContext(), R.string.no_internet, Toast.LENGTH_SHORT).show()
                }
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

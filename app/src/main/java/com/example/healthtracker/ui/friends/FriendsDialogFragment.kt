package com.example.healthtracker.ui.friends

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthtracker.R
import com.example.healthtracker.databinding.PopupFriendsBinding

class FriendsDialogFragment : DialogFragment() {
    private var _binding: PopupFriendsBinding? = null
    private val binding get() = _binding!!

    val friendListAdapter = FriendListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PopupFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.custom_rounded_background)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            textInputLayout.helperText= "current friend mode on"
            usernameInput.setOnFocusChangeListener { _, hasFocus ->
                val color = if (hasFocus) (ContextCompat.getColor(
                    requireContext(),
                    R.color.light_green
                )) else (ContextCompat.getColor(requireContext(), R.color.input_grey))
                textInputLayout.setEndIconTintList(ColorStateList.valueOf(color))

                searchSwitch.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            }
            friendRecycler.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = friendListAdapter
            }
            var newFriend = false
            searchSwitch.apply {
                setOnClickListener {
                    if (!newFriend) {
                        newFriend = true
                        rotateView(searchSwitch, 45F)
                        textInputLayout.helperText= "new friend mode on"
                    } else {
                        newFriend = false
                        rotateView(searchSwitch, 0F)
                        textInputLayout.helperText= "current friend mode on"
                    }
                }
                close.setOnClickListener {
                    dismiss()
                }
            }
        }

    }

    fun rotateView(imageView: View, angle: Float) {
        val rotationAnim = ObjectAnimator.ofFloat(imageView, "rotation", angle)
        rotationAnim.duration = 300
        rotationAnim.interpolator =
            AccelerateDecelerateInterpolator()
        rotationAnim.start()
    }
}
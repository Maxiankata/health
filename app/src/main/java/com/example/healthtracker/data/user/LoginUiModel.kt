package com.example.healthtracker.data.user

import android.view.View
import com.example.healthtracker.ui.login.LoginFragmentViewModel

data class LoginUiModel(
    val shouldNavigate: Boolean = false,
    val message: String? = null,
    val loadingVisibility: Int = View.GONE
)
object LoginUiMapper {
    fun map(state: LoginFragmentViewModel.State) = when(state) {
        is LoginFragmentViewModel.State.LoggedIn -> LoginUiModel(state.flag)
        is LoginFragmentViewModel.State.Notify -> LoginUiModel(message = state.message, loadingVisibility = state.visibility)
        is LoginFragmentViewModel.State.Loading -> LoginUiModel(loadingVisibility = state.loadingVisibility)
    }
}

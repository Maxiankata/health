package com.example.healthtracker.data.user

import com.example.healthtracker.ui.login.LoginFragmentViewModel

data class LoginUiModel(
    val shouldNavigate: Boolean = false,
    val message: String? = null
)
object LoginUiMapper {
    fun map(state: LoginFragmentViewModel.State) = when(state) {
        is LoginFragmentViewModel.State.LoggedIn -> LoginUiModel(state.flag)
        is LoginFragmentViewModel.State.Notify -> LoginUiModel(message = state.message)
    }
}
package io.realworld.android.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import io.realworld.android.AuthViewModel
import io.realworld.android.databinding.FragmentLoginSignupBinding


class SignupFragment : Fragment() {

    private var _binding: FragmentLoginSignupBinding? = null
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginSignupBinding.inflate(inflater, container, false)
        _binding?.usernameEditText?.isVisible = true
        _binding?.loginSignupButton?.text = "SIGNUP"
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.apply {
            loginSignupButton.setOnClickListener {
                authViewModel.signup(
                    usernameEditText.text.toString(),
                    emailEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
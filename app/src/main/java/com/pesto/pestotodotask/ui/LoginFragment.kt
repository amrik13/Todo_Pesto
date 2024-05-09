package com.pesto.pestotodotask.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.pesto.pestotodotask.R
import com.pesto.pestotodotask.databinding.FragmentLoginBinding
import com.pesto.pestotodotask.utils.PrefUtils
import com.pesto.pestotodotask.utils.TodoDBUtils
import kotlinx.coroutines.launch
import java.util.UUID

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val TAG = LoginFragment::class.java.simpleName
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        initUI()
        return binding.root
    }

    private fun initUI() {
        requireActivity().onBackPressedDispatcher.addCallback {
            navController.popBackStack()
            requireActivity().finish()
        }
        navController = Navigation.findNavController(requireActivity(), R.id.nav_graph_host)
        binding.registerText.setOnClickListener {
            navController.navigate(R.id.action_login_to_register)
        }
        binding.loginButton.setOnClickListener {
            val password = binding.passwordInput.text ?: ""
            val email = binding.emailInput.text ?: ""
            Log.d(TAG, "password: $password, email: $email")
            if (password.isEmpty() || email.isEmpty()) {
                Toast.makeText(requireContext(),
                    getString(R.string.add_task_toast_msg), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                TodoDBUtils.checkLogin(email.toString(), password.toString(), this@LoginFragment)
            }

        }
    }

    fun observeLoginCheck(isSuccess: Boolean, userId: String) {
        if (isSuccess) {
            Toast.makeText(
                requireContext(),
                getString(R.string.logged_in_successfully), Toast.LENGTH_SHORT
            ).show()
            PrefUtils.setUserLoggedIn(requireContext(), true)
            PrefUtils.setUserId(requireContext(), userId)
            navController.navigate(R.id.action_login_to_home)
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.incorrect_email_or_password), Toast.LENGTH_SHORT
            ).show()
        }
    }

}
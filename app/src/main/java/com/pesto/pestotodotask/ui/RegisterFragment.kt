package com.pesto.pestotodotask.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.pesto.pestotodotask.R
import com.pesto.pestotodotask.databinding.FragmentRegisterBinding
import com.pesto.pestotodotask.utils.TodoDBUtils
import kotlinx.coroutines.launch
import java.util.UUID

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val TAG = RegisterFragment::class.java.simpleName
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        initUI()
        return binding.root
    }

    private fun initUI() {
        requireActivity().onBackPressedDispatcher.addCallback {
            navController.popBackStack()
        }
        navController = Navigation.findNavController(requireActivity(), R.id.nav_graph_host)

        binding.registerButton.setOnClickListener {
            val name = binding.nameInput.text ?: ""
            val password = binding.passwordInput.text ?: ""
            val email = binding.emailInput.text ?: ""
            Log.d(TAG, "name: $name, password: $password, email: $email")
            if (name.isEmpty() || password.isEmpty() || email.isEmpty()) {
                Toast.makeText(requireContext(),
                    getString(R.string.add_task_toast_msg), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                val userId = email
                TodoDBUtils.setUser(userId.toString(), name.toString(), email.toString(), password.toString())
                Toast.makeText(requireContext(),
                    getString(R.string.registered_successfully), Toast.LENGTH_SHORT).show()
                navController.navigate(R.id.action_register_to_login)
            }

        }
        binding.registerButton.isEnabled = false
        binding.confirmPasswordInput.addTextChangedListener {
            if (it.toString() == binding.passwordInput.text.toString()) {
                binding.confirmPasswordInput.background = ContextCompat.getDrawable(requireContext(), R.drawable.input_field_bg)
                binding.registerButton.isEnabled = true
            } else {
                binding.confirmPasswordInput.background = ContextCompat.getDrawable(requireContext(), R.drawable.error_bg)
                binding.registerButton.isEnabled = false
            }
        }
    }

}
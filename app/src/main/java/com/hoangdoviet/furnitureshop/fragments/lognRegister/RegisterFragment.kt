package com.hoangdoviet.furnitureshop.fragments.lognRegister

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.hoangdoviet.furnitureshop.R
import com.hoangdoviet.furnitureshop.data.User
import com.hoangdoviet.furnitureshop.databinding.FragmentRegisterBinding
import com.hoangdoviet.furnitureshop.util.RegisterValidation
import com.hoangdoviet.furnitureshop.util.Resource
import com.hoangdoviet.furnitureshop.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val TAG = "RegisterFragment"

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    //Đây là cách Hilt cung cấp một instance của RegisterViewModel cho Fragment. Với by viewModels(), Hilt sẽ tự động tạo ra hoặc lấy lại một instance của RegisterViewModel được liên kết với vòng đời của Fragment.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvDoYouHaveAccount.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        binding.apply {
            buttonRegisterRegister.setOnClickListener {
                val user = User(
                    edFirstNameRegister.text.toString().trim(),
                    edLastNameRegister.text.toString().trim(),
                    edEmailRegister.text.toString().trim()
                )
                val password = edPasswordRegister.text.toString()
                viewModel.createAccountWithEmailAndPassword(user, password)
            }
        }
        lifecycleScope.launchWhenStarted { //Sử dụng coroutine để lắng nghe các thay đổi trong register state của ViewModel.
            viewModel.register.collect {//collect được sử dụng để lắng nghe các thay đổi từ Flow (ở đây là viewModel.register) và cập nhật UI dựa trên trạng thái của Resource.
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonRegisterRegister.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.buttonRegisterRegister.revertAnimation()
                    }

                    is Resource.Error -> {
                        Log.d(TAG, it.message.toString())
                        binding.buttonRegisterRegister.revertAnimation()
                    }

                    else -> Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.validation.collect { validation ->
                if (validation.email is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edEmailRegister.apply {
                            requestFocus()
                            error = validation.email.messsage
                        }
                    }
                }
                if (validation.password is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edPasswordRegister.apply {
                            requestFocus()
                            error = validation.password.messsage
                        }
                    }
                }

            }
        }
    }
}
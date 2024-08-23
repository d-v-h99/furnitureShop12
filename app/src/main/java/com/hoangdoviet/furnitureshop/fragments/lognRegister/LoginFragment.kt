package com.hoangdoviet.furnitureshop.fragments.lognRegister

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.hoangdoviet.furnitureshop.R
import com.hoangdoviet.furnitureshop.activities.ShoppingActivity
import com.hoangdoviet.furnitureshop.databinding.FragmentLoginBinding
import com.hoangdoviet.furnitureshop.dialog.setupBottomSheetDialog
import com.hoangdoviet.furnitureshop.util.Resource
import com.hoangdoviet.furnitureshop.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvDontHaveAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.apply {
            buttonLoginLogin.setOnClickListener {
                val email = edEmailLogin.text.toString().trim()
                val password = edPasswordLogin.text.toString()
                viewModel.login(email, password)
            }
        }
        binding.tvForgotPasswordLogin.setOnClickListener {
            setupBottomSheetDialog { email ->
                viewModel.resetPassword(email)
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collect{
                when (it) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        Snackbar.make(requireView(),"Reset link was sent to your email", Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(),"Error: ${it.message}", Snackbar.LENGTH_LONG).show()
                    }
                    else -> Unit

                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.login.collect{
                when(it) {
                    is Resource.Loading -> {
                        binding.buttonLoginLogin.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.buttonLoginLogin.revertAnimation()
                        Intent(requireActivity(),ShoppingActivity::class.java).also { intent: Intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            //also: Là một hàm mở rộng trong Kotlin. Nó cho phép thực hiện một hành động bổ sung trên đối tượng mà nó được gọi (trong trường hợp này là Intent) và sau đó trả về đối tượng đó.
                            // Hàm addFlags cho phép bạn thêm các cờ (flags) vào Intent. Các cờ này điều chỉnh cách thức hoạt động của Intent khi khởi động Activity.
                            //FLAG_ACTIVITY_CLEAR_TASK: Khi sử dụng cờ này, nếu Activity được khởi động nằm trong một task (nhiệm vụ) đã tồn tại, tất cả các hoạt động khác trong task đó sẽ bị xóa trước khi khởi động hoạt động mới.
                            //FLAG_ACTIVITY_NEW_TASK: Cờ này đảm bảo rằng Activity mới sẽ được khởi động trong một task mới. Nếu một task đã tồn tại cho hoạt động đó, hoạt động sẽ được khởi động trong task đó.
                            // => khi ấn back k quay về fragment login
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        binding.buttonLoginLogin.revertAnimation()
                    }
                    else -> Unit
                }
            }
        }
    }

}
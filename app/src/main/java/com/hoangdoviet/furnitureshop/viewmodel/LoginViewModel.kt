package com.hoangdoviet.furnitureshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.hoangdoviet.furnitureshop.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
    val login = _login.asSharedFlow()
    private val _resetPassword = MutableSharedFlow<Resource<String>>()
    val resetPassword = _resetPassword.asSharedFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch { _login.emit(Resource.Loading()) }
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                //  _login.emit(Resource.Success(it.user)) bi loi
                viewModelScope.launch {
                    it.user?.let {
                        _login.emit(Resource.Success(it))
                        //Hàm emit là một hàm suspend, có nghĩa là nó cần phải được gọi từ bên trong một coroutine. addOnSuccessListener và addOnFailureListener bản thân chúng không chạy trong một coroutine, vì vậy nếu bạn cố gắng gọi emit trực tiếp từ bên trong các listener này, bạn sẽ gặp lỗi vì bạn đang cố gắng gọi một hàm suspend bên ngoài một coroutine.
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _login.emit(Resource.Error(it.message.toString()))
                }
            }
    }
    fun resetPassword(email:String){
        viewModelScope.launch {
            _resetPassword.emit(Resource.Loading())
        }
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Success(email))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}
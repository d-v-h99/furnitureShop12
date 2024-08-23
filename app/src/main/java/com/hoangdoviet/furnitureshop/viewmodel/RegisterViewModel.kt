package com.hoangdoviet.furnitureshop.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.hoangdoviet.furnitureshop.data.User
import com.hoangdoviet.furnitureshop.util.Constants.USER_COLLECTION
import com.hoangdoviet.furnitureshop.util.RegisterFieldsState
import com.hoangdoviet.furnitureshop.util.RegisterValidation
import com.hoangdoviet.furnitureshop.util.Resource
import com.hoangdoviet.furnitureshop.util.validateEmail
import com.hoangdoviet.furnitureshop.util.validatePassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
//này cho hilt biết đây là viewmodel
class RegisterViewModel @Inject constructor(
    // sử dụng hilt để bơm phụ thuộc jnject FirebaseAuth vào viewmodel
    //Thể hiện của FirebaseAuth được Hilt inject vào ViewModel.
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
): ViewModel() {
    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    // Một MutableStateFlow để giữ trạng thái đăng ký của người dùng. Ban đầu nó được khởi tạo với trạng thái Unspecified.
    val register: Flow<Resource<User>> = _register
    //ột Flow công khai để các thành phần khác trong ứng dụng có thể quan sát trạng thái đăng ký.
    private val _validation = Channel<RegisterFieldsState>()
    val validation = _validation.receiveAsFlow()
    // flow công khai validation => giúp các thành phần khác có thể quan sát và phản hồi dựa trên các sự kiện này.
    fun createAccountWithEmailAndPassword(user: User, password: String) {
        if (checkValidation(user, password)) {
            runBlocking {
                //runBlocking là một hàm trong thư viện coroutines của Kotlin, được sử dụng để chạy một coroutine trên thread hiện tại và chặn nó cho đến khi coroutine hoàn thành.
                //Trong trường hợp này, runBlocking chặn thread hiện tại cho đến khi emit(Resource.Loading()) hoàn tất.
                _register.emit(Resource.Loading())
                // Sử dụng runBlocking có thể không phải là cách tốt nhất vì nó làm ngược lại với bản chất không đồng bộ của coroutines và có thể gây chặn UI. Thay vào đó, bạn có thể xem xét việc sử dụng viewModelScope.launch để khởi chạy một coroutine không chặn UI thread.
            }
            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener {
                    it.user?.let {
                       // _register.value = Resource.Success(it)
                        saveUserInfo(it.uid, user)
                    }
                    //it là đối tượng AuthResult được trả về khi tác vụ tạo tài khoản thành công.
                    //it.user là một đối tượng FirebaseUser đại diện cho người dùng đã được tạo.
                }.addOnFailureListener {
                    _register.value = Resource.Error(it.message.toString())
                }
        } else {
            val registerFieldsState = RegisterFieldsState (
                validateEmail(user.email), validatePassword(password)
            )
            runBlocking {
                _validation.send(registerFieldsState)
            }
        }
    }

    private fun checkValidation(user: User, password: String) : Boolean {
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)
        val shouldRegister =
            emailValidation is RegisterValidation.Success && passwordValidation is RegisterValidation.Success
        return shouldRegister
    }
    private fun saveUserInfo(userUid: String, user: User) {
        db.collection(USER_COLLECTION)
            .document(userUid)
            .set(user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user)
            }.addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }
    }
}
/*
* Flow: Được sử dụng để đại diện cho một chuỗi dữ liệu bất đồng bộ. Ở đây, Flow được dùng để theo dõi trạng thái đăng ký và trạng thái của các trường nhập liệu. Điều này giúp UI có thể lắng nghe và cập nhật dựa trên các thay đổi về trạng thái.

Channel: Là một công cụ để gửi các sự kiện đơn lẻ hoặc nhiều lần giữa các coroutine. Trong code này, Channel được dùng để gửi trạng thái kiểm tra hợp lệ của các trường đăng ký khi không hợp lệ, sau đó UI có thể nhận và xử lý các trạng thái này thông qua Flow.*/
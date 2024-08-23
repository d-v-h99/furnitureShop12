package com.hoangdoviet.furnitureshop.util

sealed class RegisterValidation(){ // đn các trạng thái có thể xảy ra trong quá trình validation dki
    object Success: RegisterValidation()
    data class Failed(val messsage: String): RegisterValidation()
}
data class RegisterFieldsState( // trạng thái validation các trường dl trong form dki
    val email: RegisterValidation,
    val password: RegisterValidation
)

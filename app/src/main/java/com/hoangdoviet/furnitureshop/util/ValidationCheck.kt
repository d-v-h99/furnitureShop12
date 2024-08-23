package com.hoangdoviet.furnitureshop.util

import android.util.Patterns

fun validateEmail(email: String) : RegisterValidation{
    if(email.isEmpty()) return RegisterValidation.Failed("Email khong de trong")
    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return RegisterValidation.Failed("Sai dinh dang email")
    return RegisterValidation.Success
}
fun validatePassword(password: String) : RegisterValidation{
    if(password.isEmpty()) return RegisterValidation.Failed("Mat khau khong duoc de trong")
    if (password.length < 6) return RegisterValidation.Failed("Mat khau lon hon 6 ki tu")
    return RegisterValidation.Success
}
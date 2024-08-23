package com.hoangdoviet.furnitureshop.dialog

import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hoangdoviet.furnitureshop.R
// Đây là một hàm mở rộng cho lớp Fragment, có nghĩa là bạn có thể gọi hàm này trực tiếp từ bất kỳ Fragment nào mà không cần phải định nghĩa hàm đó trong Fragment.
fun Fragment.setupBottomSheetDialog(
    onSendClick: (String) -> Unit
){
    val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
    val view = layoutInflater.inflate(R.layout.reset_passowrd_dialog, null)
    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED //STATE_EXPANDED, nghĩa là dialog sẽ hiển thị ở chế độ mở rộng toàn bộ.
    dialog.show()

    val edEmail = view.findViewById<EditText>(R.id.edResetPassword)
    val buttonSend = view.findViewById<Button>(R.id.buttonSendResetPassword)
    val buttonCancel = view.findViewById<Button>(R.id.buttonCancelResetPassword)
    buttonSend.setOnClickListener {
        val email = edEmail.text.toString().trim()
        onSendClick(email) // landa function đc goi khi nguoi dung an nut send
        dialog.dismiss()
    }

    buttonCancel.setOnClickListener {
        dialog.dismiss()
    }
}
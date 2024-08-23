package com.hoangdoviet.furnitureshop.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

// tuỳ chỉnh khoảng cách mục => tạo kc theo chiều dọc giữa các mục
class VerticalItemDecoration(private val amount: Int = 30): RecyclerView.ItemDecoration() {
//amount : khoảng cách theo đơn vị pixel
    override fun getItemOffsets(
        outRect: Rect, //à một đối tượng Rect đại diện cho các lề (khoảng trống) của một mục (item) trong RecyclerView.
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = amount
    //iều này có nghĩa là một khoảng trống sẽ được thêm vào dưới mỗi mục trong RecyclerView, với chiều cao bằng giá trị amount (mặc định là 30 pixel).
    }
}
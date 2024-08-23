package com.hoangdoviet.furnitureshop.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomeViewpagerAdapter(
    private val fragments: List<Fragment>,
    fm : FragmentManager, //, được sử dụng để quản lý các Fragment.
    lifecycle: Lifecycle // cung cấp thông tin về vòng đời của Fragment hoặc Activity chứa ViewPager2
) : FragmentStateAdapter(fm, lifecycle) {
    //FragmentStateAdapter là một lớp cơ sở cho adapter để làm việc với ViewPager2 khi các trang là Fragment.
    override fun getItemCount(): Int {
       return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}
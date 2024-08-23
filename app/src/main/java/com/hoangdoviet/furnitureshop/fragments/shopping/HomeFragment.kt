package com.hoangdoviet.furnitureshop.fragments.shopping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.hoangdoviet.furnitureshop.R
import com.hoangdoviet.furnitureshop.adapters.HomeViewpagerAdapter
import com.hoangdoviet.furnitureshop.databinding.FragmentHomeBinding
import com.hoangdoviet.furnitureshop.fragments.categories.AccessoryFragment
import com.hoangdoviet.furnitureshop.fragments.categories.ChairFragment
import com.hoangdoviet.furnitureshop.fragments.categories.CupboardFragment
import com.hoangdoviet.furnitureshop.fragments.categories.FurnitureFragment
import com.hoangdoviet.furnitureshop.fragments.categories.MainCategoryFragment
import com.hoangdoviet.furnitureshop.fragments.categories.TableFragment

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categoriesFragments = arrayListOf(
            MainCategoryFragment(),
            ChairFragment(),
            CupboardFragment(),
            TableFragment(),
            AccessoryFragment(),
            FurnitureFragment()
        )
        binding.viewpagerHome.isUserInputEnabled = false // vô hiệu hoá vuốt ngang thay đổi
        val viewPager2Adapter = HomeViewpagerAdapter(categoriesFragments, childFragmentManager, lifecycle)
        binding.viewpagerHome.adapter = viewPager2Adapter
        //TabLayoutMediator được sử dụng để liên kết TabLayout với ViewPager2. Điều này có nghĩa là khi bạn chọn một tab, ViewPager2 sẽ hiển thị Fragment tương ứng, và ngược lại.
        TabLayoutMediator(binding.tabLayout, binding.viewpagerHome) {tab, position ->
            when(position) {
                0 -> tab.text = "Main"
                1 -> tab.text = "Chair"
                2 -> tab.text = "Cupboard"
                3 -> tab.text = "Table"
                4 -> tab.text = "Accessory"
                5 -> tab.text = "Furniture"
            }
        }.attach()
    }
}
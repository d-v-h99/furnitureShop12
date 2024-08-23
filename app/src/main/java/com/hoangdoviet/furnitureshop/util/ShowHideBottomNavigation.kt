package com.hoangdoviet.furnitureshop.util

import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hoangdoviet.furnitureshop.R
import com.hoangdoviet.furnitureshop.activities.ShoppingActivity

fun Fragment.hideBottomNavigation(){
    val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(R.id.bottomNavigation)
    bottomNavigationView.visibility = android.view.View.GONE
}
fun Fragment.showBottomNavigationView(){
    val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(R.id.bottomNavigation)
    bottomNavigationView.visibility = android.view.View.VISIBLE
}
package com.example.recipes.ui.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

object FragmentTransactionManager {
    fun displayFragment(
        fragmentManager: FragmentManager, fragment: Fragment,
        view: Int, mustAddToBackStack: Boolean
    ) {
        if (mustAddToBackStack) {
            fragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(view, fragment, fragment.javaClass.simpleName)
                .commitAllowingStateLoss()
        } else {
            fragmentManager.beginTransaction()
                .replace(view, fragment, fragment.javaClass.simpleName)
                .commitAllowingStateLoss()
        }
    }
}
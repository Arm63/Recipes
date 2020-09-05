package com.example.recipes.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.recipes.io.bus.BusProvider
import com.example.recipes.ui.activity.BaseActivity

abstract class BaseFragment : Fragment() {
    // ===========================================================
    // Constants
    // ===========================================================
    // ===========================================================
    // Fields
    // ===========================================================
    // ===========================================================
    // Constructors
    // ===========================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================
    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================
    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================
    // ===========================================================
    // Methods
    // ===========================================================
    protected fun hideActionBarIcon() {
        (activity as BaseActivity).hideActionBarIcon()
    }

    protected fun showActionBarIcon() {
        (activity as BaseActivity).showActionBarIcon()
    }

    protected fun setActionBarIcon() {
        (activity as BaseActivity).hideActionBarIcon()
    }

    protected fun setActionBarTitle(actionBarTitle: String?) {
        (activity as BaseActivity).setActionBarTitle(actionBarTitle)
    }
    open fun onBackPressed(): Boolean {
        return false
    }


    override fun onDestroyView() {
        super.onDestroyView()
        BusProvider.unregister(this)
    }

}
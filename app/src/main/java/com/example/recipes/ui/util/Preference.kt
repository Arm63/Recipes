@file:Suppress("DEPRECATION")

package com.example.recipes.ui.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

@Suppress("DEPRECATION")
class Preference private constructor(context: Context) {

    private val mSharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val mEditor: SharedPreferences.Editor

    fun setUserFave(key: String?, userFave: Boolean?) {
        mEditor.putBoolean(key, userFave!!)
        mEditor.apply()
    }

    fun deleteUserFave(key: String?) {
        mEditor.remove(key)
        mEditor.apply()
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    var userMail: String?
        get() = mSharedPreferences.getString(PREF_USER_MAIL, null)
        set(userMail) {
            mEditor.putString(PREF_USER_MAIL, userMail)
            mEditor.apply()
        }

    fun getUserFavorites(key: String?): Boolean {
        return mSharedPreferences.getBoolean(key, false)
    }


    var userPass: String?
        get() = mSharedPreferences.getString(PREF_USER_PASS, null)
        set(userName) {
            mEditor.putString(PREF_USER_PASS, userName)
            mEditor.apply()
        }

    // Listeners
    // ===========================================================
    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    // ===========================================================
    // Methods
    // ===========================================================
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    companion object {
        // ===========================================================
        // Constants
        // ===========================================================
        private const val PREF_USER_MAIL = "PREF_USER_MAIL"
        private const val PREF_USER_PASS = "PREF_USER_PASS"

        // ===========================================================
        // Fields
        // ===========================================================
        private var sInstance: Preference? = null
        fun getInstance(context: Context): Preference? {
            if (sInstance == null) {
                sInstance = Preference(context)
            }
            return sInstance
        }
    }

    init {
        mEditor = mSharedPreferences.edit()
    }
}
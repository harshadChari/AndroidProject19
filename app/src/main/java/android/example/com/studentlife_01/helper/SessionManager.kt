package android.example.com.studentlife_01.helper

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.util.Log


class SessionManager (context: Context) {
    // LogCat tag
    private val TAG = SessionManager::class.java.simpleName

    private var PRIVATE_MODE = 0
    private val PREF_NAME = "StudentLifeLogin"

    var editor: Editor? = null
    var _context: Context? = null

    private val KEY_IS_LOGGEDIN = "isLoggedIn"

    var sharedPref: SharedPreferences

    init{
        this._context = context
        sharedPref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = sharedPref.edit()
    }

    fun setLogin(isLoggedIn: Boolean) {

        editor?.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn)

        // commit changes
        editor?.commit()

        Log.d(TAG, "User login session modified!")
    }

    fun isLoggedIn(): Boolean {
        return sharedPref.getBoolean(KEY_IS_LOGGEDIN, false)
    }

}
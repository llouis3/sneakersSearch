package com.sneakers.search

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class SharedPreferences {
    companion object {
        const val API_CONTROLLER = "api.php"
        const val API_VERSION = "v1"
        private fun getSharedPreferences(context: Context): SharedPreferences {
            return PreferenceManager.getDefaultSharedPreferences(context)
        }

        fun setLang(context: Context, lang: String?) {
            val editor = getSharedPreferences(context).edit()
            editor.putString("lang", lang)
            editor.apply()
        }

        fun getLang(context: Context): String? {
            return getSharedPreferences(context).getString("lang", "en")
        }

        fun setWorkspace(context: Context, workspace: String?) {
            val editor = getSharedPreferences(context).edit()
            editor.putString("workspace", workspace)
            editor.apply()
        }

        fun getWorkspace(context: Context): String? {
            return getSharedPreferences(context).getString("workspace", "")
        }

        fun hasWorkspace(context: Context): Boolean {
            val workspace = getWorkspace(context)
            return workspace!!.isNotEmpty()
        }
    }
}
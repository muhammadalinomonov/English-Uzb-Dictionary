package uz.gita.a5.mydicrionary.data.local

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor

class Settings private constructor() {

    companion object {
        private lateinit var instance: Settings

        private const val SHARED_PREF = "shared_pref"
        private const val LANGUAGE = "language"
        private lateinit var pref: SharedPreferences
        private lateinit var editor: Editor

        fun getInstance(context: Context): Settings {
            pref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
            editor = pref.edit()

            if (!(::instance.isInitialized)) {
                instance = Settings()
            }
            return instance
        }
    }

    var language: String
        set(value) = editor.putString(LANGUAGE, value).apply()
        get() = pref.getString(LANGUAGE, "english").toString()
}
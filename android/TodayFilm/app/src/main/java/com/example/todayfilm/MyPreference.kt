package com.example.todayfilm

import android.content.Context
import android.content.SharedPreferences

object MyPreference {
    const val sp_name = "my_sp_storage"

    fun write(context: Context, key: String, value: String) {
        val sp: SharedPreferences = context.getSharedPreferences(sp_name, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun writeInt(context: Context, key: String, value: Int) {
        val sp: SharedPreferences = context.getSharedPreferences(sp_name, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun read(context: Context, key: String): String {
        val sp: SharedPreferences = context.getSharedPreferences(sp_name, Context.MODE_PRIVATE)
        return sp.getString(key, "") ?: ""
    }

    fun readInt(context: Context, key: String): Int {
        val sp: SharedPreferences = context.getSharedPreferences(sp_name, Context.MODE_PRIVATE)
        return sp.getInt(key, 0)
    }

    fun clear(context: Context) {
        val sp: SharedPreferences = context.getSharedPreferences(sp_name, Context.MODE_PRIVATE)
        val remover = sp.edit()
        remover.clear()
        remover.apply()

    }
}
package com.example.todayfilm

import android.content.Context
import java.io.File

fun resetData(context: Context) {
    MyPreference.writeInt(context, "isComplete", 0)
    MyPreference.writeInt(context, "imgcount", 0)
    MyPreference.write(context, "imgvids", "")

    val path = context.getExternalFilesDir(null)!!.absolutePath
    val cashPath = context.externalCacheDir!!.absolutePath

    File(path).walk().forEach {
        it.delete()
    }

    File(cashPath).walk().forEach {
        it.delete()
    }
}
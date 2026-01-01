package com.bagas.obrol

import android.content.Context
import android.os.Handler

class HandlerFactory(private val context: Context) {
    fun buildHandler(): Handler = Handler(context.mainLooper)
}
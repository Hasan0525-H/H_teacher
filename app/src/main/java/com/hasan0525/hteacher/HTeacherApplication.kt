package com.hasan0525.hteacher

import android.app.Application
import com.hasan0525.hteacher.data.AppContainer

class HTeacherApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(applicationContext)
    }
}

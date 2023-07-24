package uz.gita.a5.mydicrionary

import android.app.Application
import uz.gita.a5.mydicrionary.data.AppDatabase

class App:Application() {
    override fun onCreate() {
        super.onCreate()
        AppDatabase.init(this)
    }
}
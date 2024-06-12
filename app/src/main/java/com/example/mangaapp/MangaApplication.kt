package com.example.mangaapp

import android.app.Application
import com.example.mangaapp.data.AppContainer
import com.example.mangaapp.data.DefaultAppContainer

class MangaApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}
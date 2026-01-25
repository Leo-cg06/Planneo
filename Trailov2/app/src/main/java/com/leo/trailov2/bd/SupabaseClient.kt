package com.leo.trailov2.bd

import android.content.Context
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.FlowType
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {

    private const val SUPABASE_URL = "https://eixyokbfxcuyywbxvcft.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVpeHlva2JmeGN1eXl3Ynh2Y2Z0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQzMjkwMTIsImV4cCI6MjA3OTkwNTAxMn0.s3OaHZX_vdXKUmaVN1hVxUWuzZCDf3PNd4hMArr8vEw"

    private var contextoApp: Context? = null

    fun init(context: Context) {
        contextoApp = context.applicationContext
    }

    val client by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_KEY
        ) {
            install(Auth) {
                flowType = FlowType.PKCE
                scheme = "app"
                host = "supabase.com"

                //Para guardar el inico de sesión
                autoSaveToStorage = true
                autoLoadFromStorage = true
            }

            install(Postgrest)
        }
    }
}
package com.leo.trailov2.bd

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {

    private const val SUPABASE_URL = "https://annazyqmvxrahrhzgqil.supabase.co"
    private const val SUPABASE_KEY = "sb_publishable_kbdSS7Kazuzs9Ov0-nzgJw_zbnM0px3"

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Auth)
        install(Postgrest)
    }
}
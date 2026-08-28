package com.example.autodrum.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object JsonUtils {
    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
}

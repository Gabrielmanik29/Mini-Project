package com.gabriel0011.asesmenmobpro.model

import com.squareup.moshi.Json

data class CloudinaryResponse(
    @Json(name = "secure_url")
    val secureUrl: String
)
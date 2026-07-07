package com.example.myapplication28_5_26.models

import kotlinx.serialization.Serializable

@Serializable
data class DTOQrAuthRequest(
    val sessionId: String
)

@Serializable
data class DTOQrAuthResponse(
    val message: String
)

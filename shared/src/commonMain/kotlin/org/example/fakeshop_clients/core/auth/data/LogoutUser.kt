package org.example.fakeshop_clients.core.auth.data

import org.example.fakeshop_clients.core.auth.data.models.LogoutResponse

interface LogoutUser {
    suspend operator fun invoke(): LogoutResponse
}
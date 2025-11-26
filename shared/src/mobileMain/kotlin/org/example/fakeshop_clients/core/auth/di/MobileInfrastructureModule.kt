package org.example.fakeshop_clients.core.auth.di

import org.example.fakeshop_clients.core.auth.data.AuthTokenProvider
import org.example.fakeshop_clients.core.auth.data.MobileAuthRepository
import org.example.fakeshop_clients.core.auth.domain.AuthRepository
import org.koin.dsl.module

val mobileInfrastructureModule = module {
    single<AuthRepository>{
        MobileAuthRepository(
            mobileAuthDatasource = get(),
            authTokenProvider = AuthTokenProvider
        )
    }
    //TODO inject real token provider when impl
}
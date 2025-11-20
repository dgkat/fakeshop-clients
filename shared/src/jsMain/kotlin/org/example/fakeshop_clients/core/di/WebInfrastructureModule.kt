package org.example.fakeshop_clients.core.di

import AxiosClient
import org.example.fakeshop_clients.core.auth.data.AuthDatasource
import org.example.fakeshop_clients.core.auth.data.AuthDatasourceImpl
import org.example.fakeshop_clients.core.data.ApiClient
import org.example.fakeshop_clients.core.data.PublicAxiosCLient
import org.example.fakeshop_clients.core.data.WebAuthDatasourceImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val webInfrastructureModule = module {
    val baseUrl = "http://localhost:8080"

    single<ApiClient>(named("publicClient")) {
        PublicAxiosCLient(baseUrl)
    }

    single<AuthDatasource> {
        WebAuthDatasourceImpl(get(named("publicClient")))
    }

    single<ApiClient> {
        val authDatasource: AuthDatasource = get()
        AxiosClient(baseUrl, authDatasource)
    }
}
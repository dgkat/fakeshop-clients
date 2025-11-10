package org.example.fakeshop_clients.core.di

import org.example.fakeshop_clients.core.data.ApiClient
import org.example.fakeshop_clients.core.data.AxiosClient
import org.koin.dsl.module

val webInfrastructureModule = module {
    //TODO update to real url
    single<ApiClient> {
        AxiosClient("https://api.restful-api.dev")
    }
}
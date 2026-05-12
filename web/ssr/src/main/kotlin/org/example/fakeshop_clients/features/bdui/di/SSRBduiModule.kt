package org.example.fakeshop_clients.features.bdui.di

import org.example.fakeshop_clients.features.bdui.data.SSRBduiTemplateDatasource
import org.example.fakeshop_clients.features.bdui.data.SSRBduiTemplateDatasourceImpl
import org.example.fakeshop_clients.features.bdui.data.mappers.DataToDomainBduiTemplateMapper
import org.koin.dsl.module

val ssrBduiModule = module {
    single<SSRBduiTemplateDatasource> {
        SSRBduiTemplateDatasourceImpl(
            safeApiClient = get(),
            baseUrl = get()
        )
    }

    single { DataToDomainBduiTemplateMapper() }
}

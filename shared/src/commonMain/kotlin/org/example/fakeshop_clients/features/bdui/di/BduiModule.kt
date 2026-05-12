package org.example.fakeshop_clients.features.bdui.di

import org.example.fakeshop_clients.features.bdui.data.BduiTemplateDatasource
import org.example.fakeshop_clients.features.bdui.data.BduiTemplateDatasourceImpl
import org.example.fakeshop_clients.features.bdui.data.BduiTemplateRepositoryImpl
import org.example.fakeshop_clients.features.bdui.data.mappers.DataToDomainBduiTemplateMapper
import org.example.fakeshop_clients.features.bdui.domain.BduiTemplateRepository
import org.example.fakeshop_clients.features.bdui.domain.BduiTemplateService
import org.example.fakeshop_clients.features.bdui.domain.BduiTemplateServiceImpl
import org.koin.dsl.module

val bduiModule = module {
    factory { DataToDomainBduiTemplateMapper() }

    factory<BduiTemplateDatasource> {
        BduiTemplateDatasourceImpl(authClient = get(), baseUrl = get())
    }

    factory<BduiTemplateRepository> {
        BduiTemplateRepositoryImpl(datasource = get(), mapper = get())
    }

    factory<BduiTemplateService> {
        BduiTemplateServiceImpl(repository = get())
    }
}

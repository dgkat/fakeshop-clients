package org.example.fakeshop_clients.features.bdui.di

import org.example.fakeshop_clients.features.bdui.data.BduiActionDatasource
import org.example.fakeshop_clients.features.bdui.data.BduiActionDatasourceImpl
import org.example.fakeshop_clients.features.bdui.data.BduiTemplateDatasource
import org.example.fakeshop_clients.features.bdui.data.BduiTemplateDatasourceImpl
import org.example.fakeshop_clients.features.bdui.data.BduiTemplateRepositoryImpl
import org.example.fakeshop_clients.features.bdui.data.ReplaceDatasource
import org.example.fakeshop_clients.features.bdui.data.ReplaceDatasourceImpl
import org.example.fakeshop_clients.features.bdui.data.ReplaceRepositoryImpl
import org.example.fakeshop_clients.features.bdui.data.mappers.DataToDomainBduiTemplateMapper
import org.example.fakeshop_clients.features.bdui.data.mappers.DataToDomainReplaceBindingMapper
import org.example.fakeshop_clients.features.bdui.data.mappers.DataToDomainReplaceDataMapper
import org.example.fakeshop_clients.features.bdui.data.mappers.DataToDomainReplaceLayoutMapper
import org.example.fakeshop_clients.features.bdui.domain.BduiActionService
import org.example.fakeshop_clients.features.bdui.domain.BduiActionServiceImpl
import org.example.fakeshop_clients.features.bdui.domain.BduiTemplateRepository
import org.example.fakeshop_clients.features.bdui.domain.BduiTemplateService
import org.example.fakeshop_clients.features.bdui.domain.BduiTemplateServiceImpl
import org.example.fakeshop_clients.features.bdui.domain.ReplaceRepository
import org.example.fakeshop_clients.features.bdui.domain.ReplaceService
import org.example.fakeshop_clients.features.bdui.domain.ReplaceServiceImpl
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

    factory<BduiActionDatasource> {
        BduiActionDatasourceImpl(authClient = get(), baseUrl = get())
    }

    factory<BduiActionService> {
        BduiActionServiceImpl(datasource = get())
    }

    // Replace feature (client-side slot swap)
    factory { DataToDomainReplaceBindingMapper() }
    factory { DataToDomainReplaceLayoutMapper() }
    factory { DataToDomainReplaceDataMapper() }

    factory<ReplaceDatasource> {
        ReplaceDatasourceImpl(authClient = get(), baseUrl = get())
    }

    factory<ReplaceRepository> {
        ReplaceRepositoryImpl(
            datasource = get(),
            bindingMapper = get(),
            layoutMapper = get(),
            dataMapper = get()
        )
    }

    factory<ReplaceService> {
        ReplaceServiceImpl(repository = get())
    }
}

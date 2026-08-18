package org.example.fakeshop_clients.features.recommendations.di

import org.example.fakeshop_clients.features.recommendations.data.RecommendationsDatasource
import org.example.fakeshop_clients.features.recommendations.data.RecommendationsDatasourceImpl
import org.example.fakeshop_clients.features.recommendations.data.RecommendationsRepositoryImpl
import org.example.fakeshop_clients.features.recommendations.domain.RecommendationsRepository
import org.example.fakeshop_clients.features.recommendations.domain.RecommendationsService
import org.example.fakeshop_clients.features.recommendations.domain.RecommendationsServiceImpl
import org.koin.dsl.module

val recommendationsModule = module {

    factory<RecommendationsDatasource> {
        RecommendationsDatasourceImpl(
            authClient = get(),
            baseUrl = get()
        )
    }

    factory<RecommendationsRepository> {
        RecommendationsRepositoryImpl(
            datasource = get(),
            briefProductMapper = get()
        )
    }

    factory<RecommendationsService> {
        RecommendationsServiceImpl(
            repository = get()
        )
    }
}

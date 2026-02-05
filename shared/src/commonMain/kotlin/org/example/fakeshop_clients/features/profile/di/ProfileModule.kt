package org.example.fakeshop_clients.features.profile.di

import org.example.fakeshop_clients.features.profile.data.ProfileDatasource
import org.example.fakeshop_clients.features.profile.data.ProfileDatasourceImpl
import org.example.fakeshop_clients.features.profile.data.ProfileRepositoryImpl
import org.example.fakeshop_clients.features.profile.data.mappers.DataToDomainUserInfoMapper
import org.example.fakeshop_clients.features.profile.domain.ProfileRepository
import org.example.fakeshop_clients.features.profile.domain.ProfileService
import org.example.fakeshop_clients.features.profile.domain.ProfileServiceImpl
import org.koin.dsl.module

val profileModule = module {

    factory<ProfileDatasource> {
        ProfileDatasourceImpl(
            authClient = get(),
            baseUrl = get()
        )
    }

    factory<DataToDomainUserInfoMapper> { DataToDomainUserInfoMapper() }

    factory<ProfileRepository> {
        ProfileRepositoryImpl(
            profileDatasource = get(),
            logoutUser = get(),
            dataToDomainUserInfoMapper = get()
        )
    }

    factory<ProfileService> {
        ProfileServiceImpl(
            profileRepository = get(),
            authRepository = get()
        )
    }
}
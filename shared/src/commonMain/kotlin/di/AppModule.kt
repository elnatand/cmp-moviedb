package di

import network.createHttpClient
import org.koin.dsl.module

val appModule = module {
    single { createHttpClient() }
}
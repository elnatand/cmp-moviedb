package di

import io.ktor.client.engine.darwin.Darwin
import org.koin.dsl.module

val appModule = module {
    single { Darwin.create() }
}

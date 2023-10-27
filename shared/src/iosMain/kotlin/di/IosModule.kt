package di

import io.ktor.client.engine.darwin.Darwin
import org.koin.dsl.module

val iOSappModule = module {
    single { Darwin.create() }
}

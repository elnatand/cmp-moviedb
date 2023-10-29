package di

fun initKoin() {
    initKoin(
        appDeclaration = {
            modules(iOSModule)
        }
    )
}
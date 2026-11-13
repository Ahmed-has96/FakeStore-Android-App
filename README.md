# FakeStore · Android

En butikkapp i Kotlin. Du kan bla i en produktkatalog, lagre favoritter og legge varer i handlekurven. Når et kjøp bekreftes i appen, blir det lagt i den lokale ordrehistorikken.

## To deler av koden

- **Hente produkter:** [ProductRepository](app/src/main/java/com/example/fakestore/Api/ProductRepository.kt) henter katalogen fra FakeStore API med Retrofit. [ProductViewModel](app/src/main/java/com/example/fakestore/Api/ProductViewModel.kt) gjør produktlisten tilgjengelig for [hovedskjermen](app/src/main/java/com/example/fakestore/MainActivity.kt) gjennom LiveData.
- **Lagre lokalt:** [ProduktSQLiteHjelper](app/src/main/java/com/example/fakestore/sql/ProduktSQLiteHjelper.kt) håndterer blant annet favoritter og kjøpshistorikk i SQLite. [Handlekurv](app/src/main/java/com/example/fakestore/ui/Handlekurv.kt) viser hvordan en bekreftet handlekurv flyttes til historikken.

## Kjør lokalt

Åpne prosjektroten i Android Studio med JDK 17, installer Android SDK Platform 33 og synkroniser Gradle. Kjør modulen `app` på en emulator eller telefon med Android API 22 eller nyere. Produktkatalogen trenger nett ved første innlasting.

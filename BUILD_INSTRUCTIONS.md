# Сборка Poco X3 Pro Simulator

Проект подготовлен как Android-проект. Для получения APK нужна среда с Android SDK и Gradle.

## Вариант через Android Studio
1. Открой папку `Poco_X3_Pro_Simulator` как проект.
2. Дождись синхронизации Gradle.
3. Выбери `Build > Build APK(s)`.
4. Полученный APK можно использовать как `base.apk` для APKS-архива.

## Важно
Этот ZIP сам по себе не является APKS. Переименование ZIP в `.apks` до сборки не превратит исходники в APK.

## Аватар
В проект добавлен `app/src/main/res/drawable/avatar.xml` как временный встроенный аватар.
Если нужен именно твой аватар, его PNG/JPG нужно добавить в `res/drawable` и подключить в интерфейсе.

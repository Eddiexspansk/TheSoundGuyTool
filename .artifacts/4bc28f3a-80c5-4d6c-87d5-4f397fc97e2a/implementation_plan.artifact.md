# Migración de SoundGuyToolkit a Kotlin

Este plan detalla los pasos necesarios para convertir el proyecto de Java a Kotlin, manteniendo la funcionalidad actual y siguiendo las mejores prácticas de Android.

## Proposed Changes

### Build Configuration

Actualización de los archivos Gradle para incluir el soporte de Kotlin.

#### [MODIFY] [build.gradle](file:///C:/Users/spans/AndroidStudioProjects/TheSoundGuyTool/build.gradle)
- Añadir el plugin de Kotlin en el bloque de `plugins`.

#### [MODIFY] [app/build.gradle](file:///C:/Users/spans/AndroidStudioProjects/TheSoundGuyTool/app/build.gradle)
- Aplicar el plugin de Kotlin (`id 'org.jetbrains.kotlin.android'`).
- Añadir la dependencia de la biblioteca estándar de Kotlin.
- Configurar las opciones de compilación para Kotlin.

---

### Source Code Migration

Conversión de todas las clases de Java a Kotlin. Los archivos originales de Java serán eliminados después de la conversión exitosa.

#### [NEW] [DipswitchActivity.kt](file:///C:/Users/spans/AndroidStudioProjects/TheSoundGuyTool/app/src/main/java/com/example/soundguytoolkit/DipswitchActivity.kt)
#### [DELETE] [DipswitchActivity.java](file:///C:/Users/spans/AndroidStudioProjects/TheSoundGuyTool/app/src/main/java/com/example/soundguytoolkit/DipswitchActivity.java)

#### [NEW] [DistanciaActivity.kt](file:///C:/Users/spans/AndroidStudioProjects/TheSoundGuyTool/app/src/main/java/com/example/soundguytoolkit/DistanciaActivity.kt)
#### [DELETE] [DistanciaActivity.java](file:///C:/Users/spans/AndroidStudioProjects/TheSoundGuyTool/app/src/main/java/com/example/soundguytoolkit/DistanciaActivity.java)

#### [NEW] [MainActivity.kt](file:///C:/Users/spans/AndroidStudioProjects/TheSoundGuyTool/app/src/main/java/com/example/soundguytoolkit/MainActivity.kt)
#### [DELETE] [MainActivity.java](file:///C:/Users/spans/AndroidStudioProjects/TheSoundGuyTool/app/src/main/java/com/example/soundguytoolkit/MainActivity.java)

#### [NEW] [SonometroActivity.kt](file:///C:/Users/spans/AndroidStudioProjects/TheSoundGuyTool/app/src/main/java/com/example/soundguytoolkit/SonometroActivity.kt)
#### [DELETE] [SonometroActivity.java](file:///C:/Users/spans/AndroidStudioProjects/TheSoundGuyTool/app/src/main/java/com/example/soundguytoolkit/SonometroActivity.java)

#### [NEW] [SumaActivity.kt](file:///C:/Users/spans/AndroidStudioProjects/TheSoundGuyTool/app/src/main/java/com/example/soundguytoolkit/SumaActivity.kt)
#### [DELETE] [SumaActivity.java](file:///C:/Users/spans/AndroidStudioProjects/TheSoundGuyTool/app/src/main/java/com/example/soundguytoolkit/SumaActivity.java)

#### [NEW] [Calculadora.kt](file:///C:/Users/spans/AndroidStudioProjects/TheSoundGuyTool/app/src/main/java/com/example/soundguytoolkit/Calculadora.kt)
#### [DELETE] [calculadora.java](file:///C:/Users/spans/AndroidStudioProjects/TheSoundGuyTool/app/src/main/java/com/example/soundguytoolkit/calculadora.java)

## Verification Plan

### Automated Tests
- Ejecutar `./gradlew assembleDebug` para verificar que el proyecto compila correctamente en Kotlin.
- Ejecutar las pruebas unitarias e instrumentadas existentes (convertidas también a Kotlin).

### Manual Verification
- Desplegar la aplicación en el dispositivo físico para confirmar que las actividades se abren correctamente y las herramientas (Sonómetro, Dipswitch, etc.) funcionan como se esperaba.

# PillsApp

Aplicacion Android orientada a ayudar en la gestion de tomas de medicacion, con especial enfoque en personas mayores. La app permite registrar tomas, asociarles horario y recordatorios, realizar una foto con la camara del dispositivo y usar un modelo de vision artificial para contar las pastillas detectadas y compararlas con la dosis esperada.

## Objetivo del proyecto

PillsApp busca facilitar una comprobacion sencilla de la medicacion:

- Registrar tomas con nombre, descripcion, horario y numero esperado de pastillas.
- Lanzar recordatorios locales en el dispositivo.
- Guiar al usuario en el flujo de captura de imagen.
- Analizar la foto con IA para estimar cuantas pastillas aparecen.
- Informar si la dosis detectada coincide o no con la esperada.

## Funcionalidades principales

- Gestion de tomas:
  - crear toma
  - editar toma
  - eliminar toma
  - activar o desactivar notificacion por toma

- Flujo de toma:
  - seleccionar una toma registrada
  - abrir la camara
  - hacer una foto
  - ejecutar reconocimiento de pastillas
  - mostrar confirmacion de dosis correcta o erronea

- Ajustes:
  - modo oscuro
  - modo tercera edad
  - modo pruebas para notificaciones con hora fija

- Accesibilidad:
  - pantalla "Como hacerlo" con instrucciones visuales paso a paso

- Notificaciones:
  - recordatorios locales programados con AlarmManager
  - notificacion normal del sistema cuando llega la hora

## Tecnologías usadas

- Kotlin
- Android nativo
- Jetpack Compose
- Material 3
- Navigation Compose
- Room + SQLite
- DataStore Preferences
- CameraX
- ONNX Runtime Android
- AlarmManager + BroadcastReceiver

## Estructura general del proyecto

- app/
    Contiene la aplicación Android principal desarrollada en Kotlin.
- ui/
    Incluye todas las pantallas y componentes visuales de la aplicación, como inicio, tomas, cámara, historial, confirmación y ajustes.
- data/
    Gestiona la persistencia de datos mediante Room y DataStore, incluyendo entidades, DAO, repositorios y configuración de ajustes.
- ai/
    Contiene la lógica relacionada con el reconocimiento de pastillas y la ejecución del modelo ONNX.

## Como ejecutar el proyecto

1. Abrir el proyecto en Android Studio.
2. Sincronizar Gradle.
3. Verificar permisos del dispositivo o emulador:
   - camara
   - notificaciones
   - alarmas exactas si Android lo solicita
4. Ejecutar la app en un dispositivo Android compatible.

## Como probar el flujo principal

1. Crear una toma desde la seccion de tomas.
2. Definir numero de pastillas esperado y horario.
3. Activar notificacion si se desea.
4. Ir a "Iniciar toma".
5. Seleccionar la toma.
6. Capturar una imagen con la camara.
7. Revisar el numero detectado.
8. Confirmar o repetir segun el resultado.




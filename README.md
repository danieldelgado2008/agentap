# Agenta - Gestión de Tareas Académicas

**Agenta** es una aplicación de Android diseñada para ayudar a los estudiantes a organizar sus deberes académicos. Combina la gestión eficiente de tareas con un sistema de gamificación (mascota virtual) para motivar la productividad.

---

## 1. Documentación del Proyecto

### 1.1 Arquitectura Técnica
La aplicación sigue el patrón de diseño **MVVM (Model-View-ViewModel)** y utiliza los componentes recomendados de **Android Jetpack**:

- **Model**: Base de datos **Room** para la persistencia de datos local (usuarios y tareas).
- **View**: Actividades y Fragmentos gestionados con **ViewBinding**.
- **ViewModel**: `VistaModeloTareas` que centraliza la lógica de negocio y el acceso a datos.
- **Navigation**: Jetpack Navigation Component mediante `nav_graph.xml`.

---

## 2. Funcionalidades Principales

### 2.1 Persistencia de Datos (Room Database)
- **Usuarios**: Sistema de registro e inicio de sesión local.
- **Tareas**: Gestión completa (Crear, Leer, Actualizar, Eliminar). Las tareas se guardan permanentemente en el dispositivo.

### 2.2 Gestión de Tareas
- **Pantalla de Tareas**: Visualización organizada con filtros (Próximas, Pasadas, Todas, Hechas).
- **Buscador**: Filtrado en tiempo real por título, materia o descripción.
- **Detalle de Tarea**: Vista completa de la información con opción de marcar como completada.

### 2.3 Sistema de Notificaciones
- **Recordatorios Automáticos**: La app programa recordatorios para el día anterior al vencimiento de una tarea a las 9 AM.
- **WorkManager**: Gestión eficiente de tareas en segundo plano.

### 2.4 Gamificación (La Mascota)
- **Puntos**: Completar tareas otorga 5 puntos al usuario.
- **Tienda de Accesorios**: Uso de puntos para comprar y equipar accesorios (sombrero, lentes, mazo, corona, etc.) al dinosaurio mascota.
- **Persistencia**: Los accesorios comprados se guardan en las preferencias del usuario.

### 2.5 Personalización
- **Configuración de Fondo**: Permite cambiar el color de fondo de la aplicación entre varias opciones predefinidas.

---

## 3. Requisitos Técnicos
- **SDK Mínimo**: API 24 (Android 7.0).
- **SDK de Compilación**: API 36/37.
- **Dependencias Clave**: Room, Navigation Component, WorkManager, Material Components, Kotlin Coroutines.

---

## 4. Guía de Inicio Rápido
1.  **Registro**: Crea una cuenta desde la pantalla inicial ("¿No tienes cuenta? Regístrate").
2.  **Inicio de Sesión**: Ingresa con tu usuario y contraseña.
3.  **Añadir Tareas**: Usa el botón flotante (+) para registrar nuevas actividades.
4.  **Ganar Puntos**: Completa tus tareas para acumular puntos.
5.  **Cuidar Mascota**: Visita la pantalla del dinosaurio para ver tu progreso y comprar accesorios.

---

## 5. Código Comentado
Todo el código fuente en `app/src/main/java` cuenta con comentarios detallados en **español**, explicando la función de cada clase, método y bloque de lógica compleja para facilitar su mantenimiento.

package com.example.agenta.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.agenta.R
import com.example.agenta.databinding.ActivityMainBinding

/**
 * Actividad principal que actúa como contenedor de los fragmentos de la aplicación.
 * Gestiona la navegación, la barra de herramientas (Toolbar), notificaciones y permisos.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    // Lanzador para solicitar permisos de notificaciones (requerido en Android 13+)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(this, "Las notificaciones están desactivadas", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Habilitar diseño de borde a borde (edge-to-edge)
        enableEdgeToEdge()

        // Inicializar ViewBinding para acceder a las vistas del layout activity_main
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Aplicar color de fondo personalizado
        val prefs = getSharedPreferences("Settings", MODE_PRIVATE)
        val colorHex = prefs.getString("backgroundColor", "#FFFFFF") ?: "#FFFFFF"
        try {
            binding.main.setBackgroundColor(Color.parseColor(colorHex))
        } catch (_: IllegalArgumentException) {
            binding.main.setBackgroundColor(Color.WHITE)
        }

        // Ajustar el padding para que el contenido no quede oculto bajo las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar la barra de herramientas (Toolbar)
        setSupportActionBar(binding.toolbar)

        // Configurar el NavHostFragment para la navegación entre fragmentos
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController

        // Configurar la barra de navegación con el controlador de navegación
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Verificar si se debe navegar directamente al fragmento de registro (desde LoginActivity)
        if (intent.getBooleanExtra("goToRegister", false)) {
            navController.navigate(R.id.FragmentoRegistro)
        }

        // Configurar el botón flotante (FAB) para ir a la pantalla de la mascota
        binding.fab.setImageResource(R.drawable.ic_dinosaurio_boton)
        binding.fab.setOnClickListener {
            navController.navigate(R.id.FragmentoMascota)
        }

        // Inicialización de funciones de notificaciones
        crearCanalNotificaciones()
        solicitarPermisoNotificaciones()
    }

    /**
     * Crea un canal de notificaciones necesario para mostrar alertas en Android 8.0+.
     */
    private fun crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Recordatorios de Tareas"
            val descriptionText = "Notificaciones para entregas de tareas"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("CANAL_TAREAS", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Solicita permiso para enviar notificaciones en Android 13+.
     */
    private fun solicitarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    /**
     * Infla el menú de opciones en la barra de herramientas.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    /**
     * Maneja la selección de items en el menú superior.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return when (item.itemId) {
            R.id.menu_configuracion -> {
                navController.navigate(R.id.FragmentoConfiguracion)
                true
            }
            R.id.menu_perfil -> {
                navController.navigate(R.id.FragmentoPerfil)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Maneja la acción de "ir hacia atrás" en la barra de herramientas.
     */
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Si estamos en registro, al volver atrás cerramos la actividad para regresar al Login real
        if (navController.currentDestination?.id == R.id.FragmentoRegistro) {
            finish()
            return true
        }
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}

package com.example.agenta.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.agenta.R
import com.example.agenta.databinding.ActivityMainBinding
import com.example.agenta.models.VistaModeloTareas

/**
 * Esta es la pantalla principal o el "contenedor" de toda la aplicación.
 * Imaginalo como el marco de un cuadro: aquí se muestran las tareas, el perfil y la configuración.
 * Se encarga de manejar el menú superior, las notificaciones y el botón de la mascota.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: VistaModeloTareas

    // Esta parte se encarga de preguntar al usuario si nos deja enviarle notificaciones
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(this, "No podremos avisarte de tus tareas", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Hace que el diseño ocupe toda la pantalla, incluyendo la zona de la batería y la hora
        enableEdgeToEdge()

        // Preparamos el diseño visual para poder usar sus elementos en el código
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Iniciamos el "cerebro" de datos (ViewModel) para esta pantalla
        viewModel = ViewModelProvider(this)[VistaModeloTareas::class.java]
        
        // PASO 1: Identificar quién está usando la app.
        // Buscamos en la memoria del celular el ID del usuario que inició sesión.
        val userPrefs = getSharedPreferences("UserSettings", Context.MODE_PRIVATE)
        val userId = userPrefs.getInt("currentUserId", -1)
        if (userId != -1) {
            // Le decimos al cerebro de datos: "Carga las tareas de este usuario"
            viewModel.setUsuarioId(userId)
        }

        // PASO 2: Fondo personalizado.
        // Aplicamos el color de fondo que el usuario haya guardado en ajustes.
        val prefs = getSharedPreferences("Settings", MODE_PRIVATE)
        val colorHex = prefs.getString("backgroundColor", "#FFFFFF") ?: "#FFFFFF"
        try {
            binding.main.setBackgroundColor(Color.parseColor(colorHex))
        } catch (_: IllegalArgumentException) {
            binding.main.setBackgroundColor(Color.WHITE)
        }

        // Ajustamos los márgenes para que los botones no queden tapados por los bordes del celular
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ponemos la barra de herramientas arriba con el nombre de la app
        setSupportActionBar(binding.toolbar)

        // PASO 3: Control de navegación.
        // Aquí configuramos el sistema que permite cambiar entre pantallas (Tareas, Perfil, etc.)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController

        // Esto hace que aparezca la flechita de "atrás" cuando navegamos por la app
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Si venimos del Login y presionamos "Registrarse", nos manda directo a esa pantalla
        if (intent.getBooleanExtra("goToRegister", false)) {
            navController.navigate(R.id.FragmentoRegistro)
        }

        // PASO 4: El botón circular del dinosaurio (FAB).
        // Al darle clic, te lleva a ver a tu mascota.
        binding.fab.setImageResource(R.drawable.ic_dinosaurio_boton)
        binding.fab.setOnClickListener {
            navController.navigate(R.id.FragmentoMascota)
        }

        // Preparamos el sistema para poder enviar avisos al celular
        crearCanalNotificaciones()
        solicitarPermisoNotificaciones()
    }

    /**
     * Crea un "canal" de comunicación para las notificaciones.
     * Es como registrar la app con el sistema de avisos del celular.
     */
    private fun crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Recordatorios de Tareas"
            val descriptionText = "Avisos para que no olvides tus entregas"
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
     * Pide permiso al usuario para mostrar notificaciones (obligatorio en versiones nuevas de Android).
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
     * Crea el menú de los tres puntitos o iconos en la esquina de arriba.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    /**
     * Decide qué hacer cuando el usuario toca una opción del menú (Configuración o Perfil).
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
     * Controla qué pasa cuando tocas el botón de "Atrás" en la barra de arriba.
     */
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Si estamos en registro, al volver atrás cerramos la actividad
        if (navController.currentDestination?.id == R.id.FragmentoRegistro) {
            finish()
            return true
        }
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}

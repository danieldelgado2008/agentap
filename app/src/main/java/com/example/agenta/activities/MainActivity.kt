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

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: VistaModeloTareas

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(this, "Las notificaciones están desactivadas", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[VistaModeloTareas::class.java]
        val userPrefs = getSharedPreferences("UserSettings", Context.MODE_PRIVATE)
        val userId = userPrefs.getInt("currentUserId", -1)
        if (userId != -1) {
            viewModel.setUsuarioId(userId)
        }

        val prefs = getSharedPreferences("Settings", MODE_PRIVATE)
        val colorHex = prefs.getString("backgroundColor", "#FFFFFF") ?: "#FFFFFF"
        try {
            binding.main.setBackgroundColor(Color.parseColor(colorHex))
        } catch (_: IllegalArgumentException) {
            binding.main.setBackgroundColor(Color.WHITE)
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        if (intent.getBooleanExtra("goToRegister", false)) {
            navController.navigate(R.id.FragmentoRegistro)
        }

        binding.fab.setImageResource(R.drawable.ic_dinosaurio_boton)
        binding.fab.setOnClickListener {
            navController.navigate(R.id.FragmentoMascota)
        }

        crearCanalNotificaciones()
        solicitarPermisoNotificaciones()
    }

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

    private fun solicitarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

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

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        if (navController.currentDestination?.id == R.id.FragmentoRegistro) {
            finish()
            return true
        }
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}

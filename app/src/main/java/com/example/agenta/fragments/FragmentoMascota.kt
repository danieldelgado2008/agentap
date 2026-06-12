package com.example.agenta.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.agenta.R
import com.example.agenta.models.VistaModeloTareas

/**
 * Fragmento que gestiona la personalización de la mascota.
 * Los puntos y accesorios están ligados al ID del usuario actual.
 */
class FragmentoMascota : Fragment() {

    private lateinit var viewModel: VistaModeloTareas

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragmento_mascota, container, false)
        viewModel = ViewModelProvider(requireActivity())[VistaModeloTareas::class.java]

        val tvPuntos = view.findViewById<TextView>(R.id.tvPuntos)

        // Obtenemos el ID del usuario para diferenciar sus datos de otros usuarios
        val userId = viewModel.getUsuarioId() ?: -1

        // SharedPreferences únicas por usuario usando su ID como sufijo
        val statsPrefs = requireActivity().getSharedPreferences("UserStats_$userId", Context.MODE_PRIVATE)
        val cosmeticPrefs = requireActivity().getSharedPreferences("Cosmetics_$userId", Context.MODE_PRIVATE)

        // Carga y muestra los puntos del usuario
        var puntos = statsPrefs.getInt("userPoints", 0)
        tvPuntos.text = "Puntos: $puntos"

        /**
         * Configura la lógica de compra y equipamiento de un accesorio.
         */
        fun setupBuy(btnId: Int, cost: Int, prefKey: String, imgId: Int) {
            val img = view.findViewById<ImageView>(imgId)
            val button = view.findViewById<Button>(btnId)
            
            // Si ya lo compró anteriormente, lo mostramos y cambiamos el texto del botón
            if (cosmeticPrefs.getBoolean(prefKey, false)) {
                img.isVisible = true
                button.text = "Equipado"
            }

            button.setOnClickListener {
                if (cosmeticPrefs.getBoolean(prefKey, false)) {
                    // Si ya es suyo, el botón alterna la visibilidad (Equipar/Desequipar)
                    img.isVisible = !img.isVisible
                } else if (puntos >= cost) {
                    // Lógica de compra: descuenta puntos y marca como comprado
                    puntos -= cost
                    statsPrefs.edit { putInt("userPoints", puntos) }
                    cosmeticPrefs.edit { putBoolean(prefKey, true) }
                    
                    img.isVisible = true
                    button.text = "Equipado"
                    tvPuntos.text = "Puntos: $puntos"
                    Toast.makeText(context, "¡Comprado!", Toast.LENGTH_SHORT).show()
                } else {
                    // No tiene suficientes puntos
                    Toast.makeText(context, "Puntos insuficientes", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Configuración de los diferentes accesorios disponibles
        setupBuy(R.id.btnBuyHat, 20, "hasHat", R.id.imgHat)
        setupBuy(R.id.btnBuyGlasses, 15, "hasGlasses", R.id.imgGlasses)
        setupBuy(R.id.btnBuyBowtie, 10, "hasBowtie", R.id.imgBowtie)
        setupBuy(R.id.btnBuyPaint, 40, "hasPaint", R.id.imgPaint)
        setupBuy(R.id.btnBuyMustache, 30, "hasMustache", R.id.imgMustache)
        setupBuy(R.id.btnBuyArmor, 150, "hasArmor", R.id.imgArmor)
        setupBuy(R.id.btnBuyCrown, 300, "hasCrown", R.id.imgCrown)

        return view
    }
}

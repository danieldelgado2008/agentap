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
import com.example.agenta.R

/**
 * Fragmento de la mascota (gamificación).
 * Muestra los puntos acumulados y permite comprar accesorios para el dinosaurio.
 */
class FragmentoMascota : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragmento_mascota, container, false)

        val tvPuntos = view.findViewById<TextView>(R.id.tvPuntos)

        // Obtener puntos acumulados de SharedPreferences (ganados al completar tareas)
        val statsPrefs = requireActivity().getSharedPreferences("UserStats", Context.MODE_PRIVATE)
        val puntos = statsPrefs.getInt("userPoints", 0)
        tvPuntos.text = "Puntos: $puntos"

        // Preferencias para guardar qué cosméticos han sido comprados
        val cosmeticPrefs = requireActivity().getSharedPreferences("Cosmetics", Context.MODE_PRIVATE)

        /**
         * Función auxiliar para configurar la lógica de compra y visualización de cada item.
         */
        fun setupBuy(btnId: Int, cost: Int, prefKey: String, imgId: Int) {
            val img = view.findViewById<ImageView>(imgId)
            val button = view.findViewById<Button>(btnId)
            
            // Si ya fue comprado, mostrarlo y cambiar texto del botón
            if (cosmeticPrefs.getBoolean(prefKey, false)) {
                img.isVisible = true
                button.text = "Equipado"
            }

            button.setOnClickListener {
                if (cosmeticPrefs.getBoolean(prefKey, false)) {
                    // Si ya se tiene, alterna entre ocultar/mostrar
                    img.isVisible = !img.isVisible
                } else if (puntos >= cost) {
                    // Si no se tiene y hay puntos suficientes: comprar
                    statsPrefs.edit { putInt("userPoints", puntos - cost) }
                    cosmeticPrefs.edit { putBoolean(prefKey, true) }
                    img.isVisible = true
                    button.text = "Equipado"
                    tvPuntos.text = "Puntos: ${puntos - cost}"
                    Toast.makeText(context, "¡Comprado!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Puntos insuficientes", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Configurar todos los items disponibles en la tienda
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

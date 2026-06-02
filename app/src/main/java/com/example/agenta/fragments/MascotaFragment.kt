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
import androidx.fragment.app.Fragment
import com.example.agenta.R

class MascotaFragment : Fragment() {

    private var puntos = 0
    private lateinit var tvPuntos: TextView
    private lateinit var imgHat: ImageView
    private lateinit var imgGlasses: ImageView
    private lateinit var imgBowtie: ImageView
    private lateinit var imgNecklace: ImageView
    private lateinit var imgCape: ImageView
    private lateinit var imgCrown: ImageView
    private lateinit var imgMustache: ImageView

    private lateinit var btnBuyHat: Button
    private lateinit var btnBuyGlasses: Button
    private lateinit var btnBuyBowtie: Button
    private lateinit var btnBuyNecklace: Button
    private lateinit var btnBuyCape: Button
    private lateinit var btnBuyCrown: Button
    private lateinit var btnBuyMustache: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mascota, container, false)

        tvPuntos = view.findViewById(R.id.tvPuntos)
        imgHat = view.findViewById(R.id.imgHat)
        imgGlasses = view.findViewById(R.id.imgGlasses)
        imgBowtie = view.findViewById(R.id.imgBowtie)
        imgNecklace = view.findViewById(R.id.imgNecklace)
        imgCape = view.findViewById(R.id.imgCape)
        imgCrown = view.findViewById(R.id.imgCrown)
        imgMustache = view.findViewById(R.id.imgMustache)

        btnBuyHat = view.findViewById(R.id.btnBuyHat)
        btnBuyGlasses = view.findViewById(R.id.btnBuyGlasses)
        btnBuyBowtie = view.findViewById(R.id.btnBuyBowtie)
        btnBuyNecklace = view.findViewById(R.id.btnBuyNecklace)
        btnBuyCape = view.findViewById(R.id.btnBuyCape)
        btnBuyCrown = view.findViewById(R.id.btnBuyCrown)
        btnBuyMustache = view.findViewById(R.id.btnBuyMustache)

        loadData()

        btnBuyHat.setOnClickListener { buyAccessory("hat", 10, imgHat, btnBuyHat) }
        btnBuyGlasses.setOnClickListener { buyAccessory("glasses", 15, imgGlasses, btnBuyGlasses) }
        btnBuyBowtie.setOnClickListener { buyAccessory("bowtie", 5, imgBowtie, btnBuyBowtie) }
        btnBuyNecklace.setOnClickListener { buyAccessory("necklace", 25, imgNecklace, btnBuyNecklace) }
        btnBuyMustache.setOnClickListener { buyAccessory("mustache", 12, imgMustache, btnBuyMustache) }
        btnBuyCape.setOnClickListener { buyAccessory("cape", 50, imgCape, btnBuyCape) }
        btnBuyCrown.setOnClickListener { buyAccessory("crown", 100, imgCrown, btnBuyCrown) }

        return view
    }

    private fun loadData() {
        val prefs = requireActivity().getSharedPreferences("UserStats", Context.MODE_PRIVATE)
        puntos = prefs.getInt("userPoints", 0)
        tvPuntos.text = "Puntos: $puntos"

        updateButtonState("hat", 10, btnBuyHat, imgHat)
        updateButtonState("glasses", 15, btnBuyGlasses, imgGlasses)
        updateButtonState("bowtie", 5, btnBuyBowtie, imgBowtie)
        updateButtonState("necklace", 25, btnBuyNecklace, imgNecklace)
        updateButtonState("mustache", 12, btnBuyMustache, imgMustache)
        updateButtonState("cape", 50, btnBuyCape, imgCape)
        updateButtonState("crown", 100, btnBuyCrown, imgCrown)
    }

    private fun updateButtonState(key: String, cost: Int, button: Button, imgView: ImageView) {
        val prefs = requireActivity().getSharedPreferences("UserStats", Context.MODE_PRIVATE)
        val hasItem = prefs.getBoolean("has_$key", false)
        if (hasItem) {
            button.text = "Comprado"
            button.setBackgroundColor(android.graphics.Color.GRAY)
            // Restore visibility if it was active
            val isVisible = prefs.getBoolean("show_$key", false)
            imgView.visibility = if (isVisible) View.VISIBLE else View.GONE
        } else {
            button.text = "$cost pts"
        }
    }

    private fun buyAccessory(key: String, cost: Int, imgView: ImageView, button: Button) {
        val prefs = requireActivity().getSharedPreferences("UserStats", Context.MODE_PRIVATE)
        val alreadyOwned = prefs.getBoolean("has_$key", false)

        if (alreadyOwned) {
            val isCurrentlyVisible = imgView.visibility == View.VISIBLE
            imgView.visibility = if (isCurrentlyVisible) View.GONE else View.VISIBLE
            prefs.edit().putBoolean("show_$key", !isCurrentlyVisible).apply()
            return
        }

        if (puntos >= cost) {
            puntos -= cost
            prefs.edit().apply {
                putInt("userPoints", puntos)
                putBoolean("has_$key", true)
                putBoolean("show_$key", true)
                apply()
            }
            tvPuntos.text = "Puntos: $puntos"
            imgView.visibility = View.VISIBLE
            button.text = "Comprado"
            button.setBackgroundColor(android.graphics.Color.GRAY)
            Toast.makeText(context, "¡Accesorio comprado!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "No tienes suficientes puntos", Toast.LENGTH_SHORT).show()
        }
    }
}

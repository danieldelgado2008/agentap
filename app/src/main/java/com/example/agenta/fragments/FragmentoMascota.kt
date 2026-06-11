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

class FragmentoMascota : Fragment() {

    private var puntos = 0
    private lateinit var tvPuntos: TextView
    private lateinit var imgHat: ImageView
    private lateinit var imgGlasses: ImageView
    private lateinit var imgBowtie: ImageView
    private lateinit var imgArmor: ImageView
    private lateinit var imgPaint: ImageView
    private lateinit var imgCrown: ImageView
    private lateinit var imgMustache: ImageView

    private lateinit var btnBuyHat: Button
    private lateinit var btnBuyGlasses: Button
    private lateinit var btnBuyBowtie: Button
    private lateinit var btnBuyArmor: Button
    private lateinit var btnBuyPaint: Button
    private lateinit var btnBuyCrown: Button
    private lateinit var btnBuyMustache: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragmento_mascota, container, false)

        tvPuntos = view.findViewById(R.id.tvPuntos)
        imgHat = view.findViewById(R.id.imgHat)
        imgGlasses = view.findViewById(R.id.imgGlasses)
        imgBowtie = view.findViewById(R.id.imgBowtie)
        imgArmor = view.findViewById(R.id.imgArmor)
        imgPaint = view.findViewById(R.id.imgPaint)
        imgCrown = view.findViewById(R.id.imgCrown)
        imgMustache = view.findViewById(R.id.imgMustache)

        btnBuyHat = view.findViewById(R.id.btnBuyHat)
        btnBuyGlasses = view.findViewById(R.id.btnBuyGlasses)
        btnBuyBowtie = view.findViewById(R.id.btnBuyBowtie)
        btnBuyArmor = view.findViewById(R.id.btnBuyArmor)
        btnBuyPaint = view.findViewById(R.id.btnBuyPaint)
        btnBuyCrown = view.findViewById(R.id.btnBuyCrown)
        btnBuyMustache = view.findViewById(R.id.btnBuyMustache)

        loadData()

        btnBuyHat.setOnClickListener { buyAccessory("hat", 20, imgHat, btnBuyHat) }
        btnBuyGlasses.setOnClickListener { buyAccessory("glasses", 15, imgGlasses, btnBuyGlasses) }
        btnBuyBowtie.setOnClickListener { buyAccessory("bowtie", 10, imgBowtie, btnBuyBowtie) }
        btnBuyArmor.setOnClickListener { buyAccessory("armor", 150, imgArmor, btnBuyArmor) }
        btnBuyPaint.setOnClickListener { buyAccessory("paint", 40, imgPaint, btnBuyPaint) }
        btnBuyCrown.setOnClickListener { buyAccessory("crown", 300, imgCrown, btnBuyCrown) }
        btnBuyMustache.setOnClickListener { buyAccessory("mustache", 30, imgMustache, btnBuyMustache) }

        return view
    }

    private fun loadData() {
        val prefs = requireActivity().getSharedPreferences("UserStats", Context.MODE_PRIVATE)
        puntos = prefs.getInt("userPoints", 0)
        tvPuntos.text = "Puntos: $puntos"

        updateButtonState("hat", 20, btnBuyHat, imgHat)
        updateButtonState("glasses", 15, btnBuyGlasses, imgGlasses)
        updateButtonState("bowtie", 10, btnBuyBowtie, imgBowtie)
        updateButtonState("armor", 150, btnBuyArmor, imgArmor)
        updateButtonState("paint", 40, btnBuyPaint, imgPaint)
        updateButtonState("crown", 300, btnBuyCrown, imgCrown)
        updateButtonState("mustache", 30, btnBuyMustache, imgMustache)
    }

    private fun updateButtonState(key: String, cost: Int, button: Button, imgView: ImageView) {
        val prefs = requireActivity().getSharedPreferences("UserStats", Context.MODE_PRIVATE)
        val hasItem = prefs.getBoolean("has_$key", false)
        if (hasItem) {
            button.text = "Comprado"
            button.setBackgroundColor(android.graphics.Color.GRAY)
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

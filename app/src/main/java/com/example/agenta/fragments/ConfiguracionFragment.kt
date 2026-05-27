package com.example.agenta.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.agenta.R
import com.example.agenta.activities.LoginActivity
import com.example.agenta.activities.MainActivity

class ConfiguracionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_configuracion, container, false)

        val btnLogout = view.findViewById<Button>(R.id.btnLogout)
        val colorWhite = view.findViewById<View>(R.id.colorWhite)
        val colorLightBlue = view.findViewById<View>(R.id.colorLightBlue)
        val colorLightPink = view.findViewById<View>(R.id.colorLightPink)
        val colorLightGreen = view.findViewById<View>(R.id.colorLightGreen)

        btnLogout.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        colorWhite.setOnClickListener { changeBackgroundColor("#FFFFFF") }
        colorLightBlue.setOnClickListener { changeBackgroundColor("#E3F2FD") }
        colorLightPink.setOnClickListener { changeBackgroundColor("#FCE4EC") }
        colorLightGreen.setOnClickListener { changeBackgroundColor("#E8F5E9") }

        return view
    }

    private fun changeBackgroundColor(colorHex: String) {
        val prefs = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)
        prefs.edit().putString("backgroundColor", colorHex).apply()
        view?.findViewById<View>(R.id.layoutConfiguracion)?.setBackgroundColor(Color.parseColor(colorHex))
        val intent = requireActivity().intent
        intent.removeExtra("goToRegister")
        requireActivity().recreate()
    }
}

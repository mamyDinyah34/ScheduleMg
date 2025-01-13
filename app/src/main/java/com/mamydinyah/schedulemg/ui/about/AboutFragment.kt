package com.mamydinyah.schedulemg.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mamydinyah.schedulemg.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.buttonFacebook.setOnClickListener {
            val facebookIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/mamydinyah34ZH/"))
            startActivity(facebookIntent)
        }

        binding.buttonWhatsApp.setOnClickListener {
            val whatsappIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/261342994790"))
            startActivity(whatsappIntent)
        }

        binding.buttonEmail.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:mamydinyah34@gmail.com"))
            startActivity(emailIntent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

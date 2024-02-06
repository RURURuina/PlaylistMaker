package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<LinearLayout>(R.id.back_button)
        val contactSupport = findViewById<LinearLayout>(R.id.contact_support)
        val termsOfUse = findViewById<LinearLayout>(R.id.terms_of_use)
        val shareApp = findViewById<LinearLayout>(R.id.share_application)


        backButton.setOnClickListener {
            finish()
        }


        contactSupport.setOnClickListener {
             val sendMail = Intent(Intent.ACTION_SENDTO)
            sendMail.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.my_email)))
            sendMail.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
            sendMail.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_text))
            sendMail.data = Uri.parse("mailto:")
            startActivity(sendMail)
        }


        termsOfUse.setOnClickListener {
            val termsIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.terms_url)))
            startActivity(termsIntent)
        }

        shareApp.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_url))
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)))
        }
    }
}
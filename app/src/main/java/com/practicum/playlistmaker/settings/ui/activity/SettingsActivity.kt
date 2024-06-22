package com.practicum.playlistmaker.settings.ui.activity

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {


    private val settingsViewModel by viewModel<SettingsViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        val backButton = findViewById<LinearLayout>(R.id.back_button)
        val contactSupport = findViewById<LinearLayout>(R.id.contact_support)
        val termsOfUse = findViewById<LinearLayout>(R.id.terms_of_use)
        val shareApp = findViewById<LinearLayout>(R.id.share_application)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.theme_switcher)

        backButton.setOnClickListener {
            finish()
        }

        settingsViewModel.getThemeLiveData().observe(this) { isChecked ->
            themeSwitcher.isChecked = isChecked
        }

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.clickSwitchTheme(isChecked)
        }

        contactSupport.setOnClickListener {
            settingsViewModel.contactSupportButton()
        }

        termsOfUse.setOnClickListener {
            settingsViewModel.openTermsButton()
        }

        shareApp.setOnClickListener {
            settingsViewModel.shareAppButton()
        }
    }

}
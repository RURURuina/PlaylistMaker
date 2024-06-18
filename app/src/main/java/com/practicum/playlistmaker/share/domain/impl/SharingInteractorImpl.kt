package com.practicum.playlistmaker.share.domain.impl

import com.practicum.playlistmaker.share.domain.ExternalNavigator
import com.practicum.playlistmaker.share.domain.SharingInteractor

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {
    override fun openTerms() {
        externalNavigator.openLink()
    }

    override fun shareApp() {
        externalNavigator.share()
    }

    override fun contactSupport() {
        externalNavigator.sendEmail()
    }
}
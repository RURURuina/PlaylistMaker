package com.practicum.playlistmaker.share.data.impl

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.share.domain.ExternalNavigator
import com.practicum.playlistmaker.share.domain.model.EmailData

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {

    private val emailData = EmailData()
    override fun share() {
        context.startActivity(
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    context.getString(R.string.share_apl)
                )
                type = "text/plain"
                flags = FLAG_ACTIVITY_NEW_TASK
            }, null
        )
    }

    override fun openLink() {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(context.getString(R.string.terms_url))
            ).setFlags(FLAG_ACTIVITY_NEW_TASK)
        )
    }

    override fun sendEmail() {
        context.startActivity(
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse(emailData.mailTo)
                putExtra(Intent.EXTRA_TEXT, context.getString(R.string.email_text))
                putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.my_email)))
                putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.email_subject))
                flags = FLAG_ACTIVITY_NEW_TASK
            }
        )
    }
}
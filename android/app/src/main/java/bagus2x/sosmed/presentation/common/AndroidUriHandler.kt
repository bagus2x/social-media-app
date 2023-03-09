package bagus2x.sosmed.presentation.common

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.platform.UriHandler
import bagus2x.sosmed.SosmedApp

class AndroidUriHandler(
    private val context: Context
) : UriHandler {

    override fun openUri(uri: String) {
        val customTabsSession = (context as? SosmedApp)
            ?.customTabsSession
            ?: (context.applicationContext as? SosmedApp)?.customTabsSession
        val builder = CustomTabsIntent.Builder(customTabsSession)
        builder.setShowTitle(true)
        builder.setInitialActivityHeightPx(700)
        builder.setToolbarCornerRadiusDp(16)
        builder.setInstantAppsEnabled(true)
        val tabIntent = builder.build()
        tabIntent.launchUrl(context, Uri.parse(uri))
    }
}

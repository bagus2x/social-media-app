package bagus2x.sosmed.presentation.common.components

import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import bagus2x.sosmed.presentation.common.AndroidUriHandler
import bagus2x.sosmed.presentation.common.LocalTranslator
import bagus2x.sosmed.presentation.common.theme.SosmedRippleTheme
import bagus2x.sosmed.presentation.common.translation.TranslatorImpl

@Composable
fun LocalProvider(
    vararg providedValue: ProvidedValue<*>,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val uriHandler = remember { AndroidUriHandler(context) }
    val translator = remember { TranslatorImpl() }
    CompositionLocalProvider(
        LocalRippleTheme provides SosmedRippleTheme,
        LocalContentAlpha provides 1f,
        LocalUriHandler provides uriHandler,
        LocalTranslator provides translator,
        * providedValue
    ) {
        content()
    }
}

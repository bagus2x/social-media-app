package bagus2x.sosmed.presentation.common

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import bagus2x.sosmed.domain.model.User
import bagus2x.sosmed.presentation.common.translation.Translator
import bagus2x.sosmed.presentation.common.translation.TranslatorImpl

val LocalShowSnackbar = staticCompositionLocalOf { { _: String -> } }

val LocalAuthenticatedUser = compositionLocalOf<User?> { null }

val LocalTranslator = staticCompositionLocalOf<Translator> { TranslatorImpl() }

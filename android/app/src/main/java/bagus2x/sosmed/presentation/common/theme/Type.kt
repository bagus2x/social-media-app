package bagus2x.sosmed.presentation.common.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import bagus2x.sosmed.R

val Roboto = GoogleFont("Roboto")
val SourceCode = GoogleFont("Source Code Pro")

val GoogleFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs_prod
)

val RobotoFontFamily = FontFamily(
    Font(
        googleFont = Roboto,
        fontProvider = GoogleFontProvider,
        weight = FontWeight.Bold,
        style = FontStyle.Normal,
    ),
    Font(
        googleFont = Roboto,
        fontProvider = GoogleFontProvider,
        weight = FontWeight.Bold,
        style = FontStyle.Italic
    ),
    Font(
        googleFont = Roboto,
        fontProvider = GoogleFontProvider,
        weight = FontWeight.Normal,
        style = FontStyle.Normal
    ),
    Font(
        googleFont = Roboto,
        fontProvider = GoogleFontProvider,
        weight = FontWeight.Normal,
        style = FontStyle.Italic
    ),
    Font(
        googleFont = Roboto,
        fontProvider = GoogleFontProvider,
        weight = FontWeight.Light,
        style = FontStyle.Normal
    ),
    Font(
        googleFont = Roboto,
        fontProvider = GoogleFontProvider,
        weight = FontWeight.Light,
        style = FontStyle.Italic
    ),
    Font(
        googleFont = Roboto,
        fontProvider = GoogleFontProvider,
        weight = FontWeight.Medium,
        style = FontStyle.Normal
    ),
    Font(
        googleFont = Roboto,
        fontProvider = GoogleFontProvider,
        weight = FontWeight.Medium,
        style = FontStyle.Italic
    ),
    Font(
        googleFont = Roboto,
        fontProvider = GoogleFontProvider,
        weight = FontWeight.ExtraLight,
        style = FontStyle.Normal
    )
    ,
    Font(
        googleFont = Roboto,
        fontProvider = GoogleFontProvider,
        weight = FontWeight.ExtraLight,
        style = FontStyle.Italic
    ),
    Font(
        googleFont = Roboto,
        fontProvider = GoogleFontProvider,
        weight = FontWeight.ExtraBold,
        style = FontStyle.Normal
    ),
    Font(
        googleFont = Roboto,
        fontProvider = GoogleFontProvider,
        weight = FontWeight.ExtraBold,
        style = FontStyle.Italic
    ),
    Font(
        googleFont = Roboto,
        fontProvider = GoogleFontProvider,
        weight = FontWeight.SemiBold,
        style = FontStyle.Normal
    ),
    Font(
        googleFont = Roboto,
        fontProvider = GoogleFontProvider,
        weight = FontWeight.SemiBold,
        style = FontStyle.Italic
    )
)

val SourceCodeFontFamily = FontFamily(
    Font(
        googleFont = SourceCode,
        fontProvider = GoogleFontProvider,
        weight = FontWeight.Bold,
        style = FontStyle.Normal,
    ),
    Font(
        googleFont = SourceCode,
        fontProvider = GoogleFontProvider,
        weight = FontWeight.Bold,
        style = FontStyle.Italic
    ),
    Font(
        googleFont = SourceCode,
        fontProvider = GoogleFontProvider,
        weight = FontWeight.Normal,
        style = FontStyle.Normal
    ),
    Font(
        googleFont = SourceCode,
        fontProvider = GoogleFontProvider,
        weight = FontWeight.Normal,
        style = FontStyle.Italic
    ),
    Font(
        googleFont = SourceCode,
        fontProvider = GoogleFontProvider,
        weight = FontWeight.Light,
        style = FontStyle.Normal
    ),
    Font(
        googleFont = SourceCode,
        fontProvider = GoogleFontProvider,
        weight = FontWeight.Light,
        style = FontStyle.Italic
    ),
    Font(
        googleFont = SourceCode,
        fontProvider = GoogleFontProvider,
        weight = FontWeight.Medium,
        style = FontStyle.Normal
    ),
    Font(
        googleFont = SourceCode,
        fontProvider = GoogleFontProvider,
        weight = FontWeight.Medium,
        style = FontStyle.Italic
    ),
    Font(
        googleFont = SourceCode,
        fontProvider = GoogleFontProvider,
        weight = FontWeight.ExtraLight,
        style = FontStyle.Normal
    )
    ,
    Font(
        googleFont = SourceCode,
        fontProvider = GoogleFontProvider,
        weight = FontWeight.ExtraLight,
        style = FontStyle.Italic
    ),
    Font(
        googleFont = SourceCode,
        fontProvider = GoogleFontProvider,
        weight = FontWeight.ExtraBold,
        style = FontStyle.Normal
    ),
    Font(
        googleFont = SourceCode,
        fontProvider = GoogleFontProvider,
        weight = FontWeight.ExtraBold,
        style = FontStyle.Italic
    ),
    Font(
        googleFont = SourceCode,
        fontProvider = GoogleFontProvider,
        weight = FontWeight.SemiBold,
        style = FontStyle.Normal
    ),
    Font(
        googleFont = SourceCode,
        fontProvider = GoogleFontProvider,
        weight = FontWeight.SemiBold,
        style = FontStyle.Italic
    )
)

// Set of Material typography styles to start with
val Typography = Typography(
    defaultFontFamily = RobotoFontFamily,
    h1 = TextStyle(
        fontWeight = FontWeight.Light,
        fontSize = 96.sp,
        letterSpacing = (-1.5).sp
    ),
    h2 = TextStyle(
        fontWeight = FontWeight.Light,
        fontSize = 60.sp,
        letterSpacing = (-0.5).sp
    ),
    h3 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 48.sp,
        letterSpacing = 0.sp
    ),
    h4 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 34.sp,
        letterSpacing = 0.25.sp
    ),
    h5 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        letterSpacing = 0.sp
    ),
    h6 = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        letterSpacing = 0.15.sp
    ),
    subtitle1 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.15.sp
    ),
    subtitle2 = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp
    ),
    body1 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp
    ),
    body2 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp
    ),
    button = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 1.25.sp
    ),
    caption = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.4.sp
    ),
    overline = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        letterSpacing = 1.5.sp
    )
)

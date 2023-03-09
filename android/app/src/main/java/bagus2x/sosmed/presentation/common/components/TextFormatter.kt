package bagus2x.sosmed.presentation.common.components

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import bagus2x.sosmed.presentation.common.theme.SourceCodeFontFamily

@Composable
fun TextFormatter(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    boldSpanStyle: SpanStyle = style.toSpanStyle().copy(fontWeight = FontWeight.Bold),
    codeSpanStyle: SpanStyle = style.toSpanStyle().copy(
        background = MaterialTheme.colors.onBackground.copy(alpha = .1f),
        fontFamily = SourceCodeFontFamily,
        fontSize = style.fontSize * .8f
    ),
    underlineSpanStyle: SpanStyle = style.toSpanStyle()
        .copy(textDecoration = TextDecoration.Underline),
    hashtagSpanStyle: SpanStyle = style.toSpanStyle().copy(color = MaterialTheme.colors.primary),
    lineThroughSpanStyle: SpanStyle = style.toSpanStyle()
        .copy(textDecoration = TextDecoration.LineThrough),
    mentionSpanStyle: SpanStyle = style.toSpanStyle().copy(color = MaterialTheme.colors.primary),
    urlSpanStyle: SpanStyle = style.toSpanStyle().copy(color = MaterialTheme.colors.primary),
    onClick: TextFormatterClickScope.() -> Unit = { }
) {
    TextFormatter(
        text = buildAnnotatedString { append(text) },
        modifier,
        color,
        fontSize,
        fontStyle,
        fontWeight,
        fontFamily,
        letterSpacing,
        textDecoration,
        textAlign,
        lineHeight,
        style,
        softWrap,
        overflow,
        maxLines,
        onTextLayout,
        boldSpanStyle,
        codeSpanStyle,
        underlineSpanStyle,
        hashtagSpanStyle,
        lineThroughSpanStyle,
        mentionSpanStyle,
        urlSpanStyle,
        onClick
    )
}

interface TextFormatterClickScope {

    fun detectClickUrl(block: (url: String) -> Unit)
    fun detectClickHashtag(block: (hashtag: String) -> Unit)
    fun detectClickMention(block: (mention: String) -> Unit)
    fun detectClickText(block: (offset: Int) -> Unit)
}

private class TextFormatterClickScopeImpl(
    annotatedString: AnnotatedString,
    private val offset: Int
) : TextFormatterClickScope {
    val annotatedString = annotatedString
        .getStringAnnotations(offset, offset)
        .firstOrNull()

    override fun detectClickUrl(block: (String) -> Unit) {
        if (annotatedString?.tag == TagAnnotation.Url.name) block(annotatedString.item)
    }

    override fun detectClickHashtag(block: (String) -> Unit) {
        if (annotatedString?.tag == TagAnnotation.Hashtag.name) block(annotatedString.item)
    }

    override fun detectClickMention(block: (String) -> Unit) {
        if (annotatedString?.tag == TagAnnotation.Mention.name) block(annotatedString.item)
    }

    override fun detectClickText(block: (offset: Int) -> Unit) {
        if (annotatedString == null) {
            block(offset)
        }
    }
}

@Composable
fun TextFormatter(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    boldSpanStyle: SpanStyle = style.toSpanStyle().copy(fontWeight = FontWeight.Bold),
    codeSpanStyle: SpanStyle = style.toSpanStyle().copy(
        background = MaterialTheme.colors.onBackground.copy(alpha = .1f),
        fontFamily = SourceCodeFontFamily,
        fontSize = style.fontSize * .8f
    ),
    underlineSpanStyle: SpanStyle = style.toSpanStyle()
        .copy(textDecoration = TextDecoration.Underline),
    hashtagSpanStyle: SpanStyle = style.toSpanStyle().copy(color = MaterialTheme.colors.primary),
    lineThroughSpanStyle: SpanStyle = style.toSpanStyle()
        .copy(textDecoration = TextDecoration.LineThrough),
    mentionSpanStyle: SpanStyle = style.toSpanStyle().copy(color = MaterialTheme.colors.primary),
    urlSpanStyle: SpanStyle = style.toSpanStyle().copy(color = MaterialTheme.colors.primary),
    onClick: TextFormatterClickScope.() -> Unit = { }
) {
    val textColor = color.takeOrElse {
        style.color.takeOrElse {
            LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
        }
    }

    val mergedStyle = style.merge(
        TextStyle(
            color = textColor,
            fontSize = fontSize,
            fontWeight = fontWeight,
            textAlign = textAlign,
            lineHeight = lineHeight,
            fontFamily = fontFamily,
            textDecoration = textDecoration,
            fontStyle = fontStyle,
            letterSpacing = letterSpacing
        )
    )

    val annotatedString = textFormatter(
        text,
        style = mergedStyle,
        boldSpanStyle,
        codeSpanStyle,
        underlineSpanStyle,
        hashtagSpanStyle,
        lineThroughSpanStyle,
        mentionSpanStyle,
        urlSpanStyle
    )

    ClickableText(
        text = annotatedString,
        modifier,
        style = mergedStyle,
        softWrap,
        overflow,
        maxLines,
        onTextLayout,
        onClick = { offset ->
            TextFormatterClickScopeImpl(annotatedString, offset).onClick()
        }
    )
}

val SymbolPattern by lazy {
    Regex("""(https?://[^\s\t\n]+)|(`[^`]+`)|(@\w+)|(#\w+)|(\*[^`]+\*)|(_[^`]+_)|(~[^`]+~)""")
}

enum class TagAnnotation {
    Bold,
    Code,
    Underline,
    Hashtag,
    LineThrough,
    Mention,
    Url
}

@Composable
fun textFormatter(
    text: String,
    style: TextStyle = LocalTextStyle.current,
    boldSpanStyle: SpanStyle = style.toSpanStyle().copy(fontWeight = FontWeight.Bold),
    codeSpanStyle: SpanStyle = style.toSpanStyle().copy(
        background = MaterialTheme.colors.onBackground.copy(alpha = .1f),
        fontFamily = SourceCodeFontFamily,
        fontSize = style.fontSize * .8f
    ),
    underlineSpanStyle: SpanStyle = style.toSpanStyle()
        .copy(textDecoration = TextDecoration.Underline),
    hashtagSpanStyle: SpanStyle = style.toSpanStyle().copy(color = MaterialTheme.colors.primary),
    lineThroughSpanStyle: SpanStyle = style.toSpanStyle()
        .copy(textDecoration = TextDecoration.LineThrough),
    mentionSpanStyle: SpanStyle = style.toSpanStyle().copy(color = MaterialTheme.colors.primary),
    webSpanStyle: SpanStyle = style.toSpanStyle().copy(color = MaterialTheme.colors.primary)
): AnnotatedString {
    return textFormatter(
        text = buildAnnotatedString { append(text) },
        style = style,
        boldSpanStyle = boldSpanStyle,
        codeSpanStyle = codeSpanStyle,
        underlineSpanStyle = underlineSpanStyle,
        hashtagSpanStyle = hashtagSpanStyle,
        lineThroughSpanStyle = lineThroughSpanStyle,
        mentionSpanStyle = mentionSpanStyle,
        webSpanStyle = webSpanStyle
    )
}

@Composable
fun textFormatter(
    text: AnnotatedString,
    style: TextStyle = LocalTextStyle.current,
    boldSpanStyle: SpanStyle = style.toSpanStyle().copy(fontWeight = FontWeight.Bold),
    codeSpanStyle: SpanStyle = style.toSpanStyle().copy(
        background = MaterialTheme.colors.onBackground.copy(alpha = .1f),
        fontFamily = SourceCodeFontFamily,
        fontSize = style.fontSize * .8f
    ),
    underlineSpanStyle: SpanStyle = style.toSpanStyle()
        .copy(textDecoration = TextDecoration.Underline),
    hashtagSpanStyle: SpanStyle = style.toSpanStyle().copy(color = MaterialTheme.colors.primary),
    lineThroughSpanStyle: SpanStyle = style.toSpanStyle()
        .copy(textDecoration = TextDecoration.LineThrough),
    mentionSpanStyle: SpanStyle = style.toSpanStyle().copy(color = MaterialTheme.colors.primary),
    webSpanStyle: SpanStyle = style.toSpanStyle().copy(color = MaterialTheme.colors.primary)
): AnnotatedString {
    return buildAnnotatedString {
        val results = SymbolPattern.findAll(text)
        var cursor = 0

        for (result in results) {
            append(text.slice(cursor until result.range.first))
            when (result.value.first()) {
                '*' -> {
                    val annotatedString = AnnotatedString(
                        text = result.value.trim('*'),
                        spanStyle = boldSpanStyle
                    )
                    addStringAnnotation(
                        tag = TagAnnotation.Bold.name,
                        annotation = annotatedString.text,
                        start = this.length,
                        end = this.length + annotatedString.length
                    )
                    append(text = annotatedString)
                }
                '`' -> {
                    val annotatedString = AnnotatedString(
                        text = result.value.trim('`'),
                        spanStyle = codeSpanStyle
                    )
                    addStringAnnotation(
                        tag = TagAnnotation.LineThrough.name,
                        annotation = annotatedString.text,
                        start = this.length,
                        end = this.length + annotatedString.length
                    )
                    append(text = annotatedString)
                }
                '#' -> {
                    val annotatedString = AnnotatedString(
                        text = result.value,
                        spanStyle = hashtagSpanStyle
                    )
                    addStringAnnotation(
                        tag = TagAnnotation.Hashtag.name,
                        annotation = annotatedString.text,
                        start = this.length,
                        end = this.length + annotatedString.length
                    )
                    append(text = annotatedString)
                }
                '~' -> {
                    val annotatedString = AnnotatedString(
                        text = result.value.trim('~'),
                        spanStyle = lineThroughSpanStyle
                    )
                    addStringAnnotation(
                        tag = TagAnnotation.Code.name,
                        annotation = annotatedString.text,
                        start = this.length,
                        end = this.length + annotatedString.length
                    )
                    append(text = annotatedString)
                }
                '_' -> {
                    val annotatedString = AnnotatedString(
                        text = result.value.trim('_'),
                        spanStyle = underlineSpanStyle
                    )
                    addStringAnnotation(
                        tag = TagAnnotation.Underline.name,
                        annotation = annotatedString.text,
                        start = this.length,
                        end = this.length + annotatedString.length
                    )
                    append(text = annotatedString)
                }
                '@' -> {
                    val annotatedString = AnnotatedString(
                        text = result.value,
                        spanStyle = mentionSpanStyle
                    )
                    addStringAnnotation(
                        tag = TagAnnotation.Mention.name,
                        annotation = annotatedString.text,
                        start = this.length,
                        end = this.length + annotatedString.length
                    )
                    append(text = annotatedString)
                }
                'h' -> {
                    val annotatedString = AnnotatedString(
                        text = result.value,
                        spanStyle = webSpanStyle
                    )
                    addStringAnnotation(
                        tag = TagAnnotation.Url.name,
                        annotation = annotatedString.text,
                        start = this.length,
                        end = this.length + annotatedString.length
                    )
                    append(text = annotatedString)
                }
                else -> append(result.value)
            }

            cursor = result.range.last + 1
        }

        if (!results.none()) {
            append(text.slice(cursor..text.lastIndex))
        } else {
            append(text)
        }
    }
}

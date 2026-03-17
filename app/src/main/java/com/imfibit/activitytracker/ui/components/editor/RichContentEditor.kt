package com.imfibit.activitytracker.ui.components.editor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.insert
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.ui.AppColors
import com.imfibit.activitytracker.ui.AppTheme

@Stable
class RichContentEditorState(initialText: String = "") {
    val textFieldState = TextFieldState(initialText)
    var isFocused by mutableStateOf(false)
    var textLayoutResult by mutableStateOf<TextLayoutResult?>(null)

    fun insertMarkdown(prefix: String, suffix: String = "") {
        textFieldState.edit {
            val start = selection.start
            val end = selection.end
            val currentText = toString()

            if (start >= 0 && end <= currentText.length && start <= end) {
                val textToWrap = currentText.substring(start, end)
                replace(start, end, prefix + textToWrap + suffix)

                val newStart = start + prefix.length
                val newEnd = newStart + textToWrap.length

                if (newEnd <= length) {
                    this.selection = TextRange(newStart, newEnd)
                }
            }
        }
    }

    fun toggleHeader() {
        textFieldState.edit {
            val textStr = toString()
            val sel = selection

            var lineStart = textStr.lastIndexOf('\n', sel.start - 1).let { if (it == -1) 0 else it + 1 }
            var lineEnd = textStr.indexOf('\n', sel.start).let { if (it == -1) textStr.length else it }

            if (lineStart >= 0 && lineEnd <= textStr.length && lineStart <= lineEnd) {
                val currentLine = textStr.substring(lineStart, lineEnd)
                val headerMatch = Regex("^(#{1,3})(.*)$").find(currentLine)

                val newLineContent: String
                if (headerMatch != null) {
                    val hashes = headerMatch.groupValues[1]
                    val textContent = headerMatch.groupValues[2]
                    newLineContent = if (hashes.length == 3) textContent else "#$hashes$textContent"
                } else {
                    newLineContent = "#$currentLine"
                }

                replace(lineStart, lineEnd, newLineContent)
            }
        }
    }

    fun setBold() = insertMarkdown("**", "**")
    fun setItalic() = insertMarkdown("/", "/")
    fun setStrikethrough() = insertMarkdown("~~", "~~")
    fun setBulletList() = insertMarkdown("- ", "")
    fun setNumberedList() = insertMarkdown("1. ", "")

    fun undo() {}
    fun redo() {}
}

@Composable
fun rememberRichContentEditorState(initialText: String = ""): RichContentEditorState {
    return remember(initialText) { RichContentEditorState(initialText) }
}

@Composable
fun RichContentEditor(
    state: RichContentEditorState,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isReadOnly: Boolean = false,
) {
    if (isReadOnly) {
        RichContentText(
            text = state.textFieldState.text.toString(),
            modifier = modifier,
            style = TextStyle(
                fontSize = 16.sp,
                color = AppTheme.colors.onSurfaceVariant,
                lineHeight = 24.sp
            )
        )
    } else {
        val appColors = AppTheme.colors

        BasicTextField(
            state = state.textFieldState,
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = appColors.onSurfaceVariant,
                lineHeight = 24.sp
            ),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            cursorBrush = SolidColor(appColors.primary),
            outputTransformation = remember(appColors) { MarkdownOutputTransformation(appColors) },
            inputTransformation = MarkdownInputTransformation,
            onTextLayout = { getResult -> state.textLayoutResult = getResult() },
            modifier = modifier.onFocusChanged { state.isFocused = it.isFocused },
            decorator = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (state.textFieldState.text.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = TextStyle(fontSize = 16.sp, color = appColors.outlineVariant)
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Composable
fun RichContentText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    hideMarkdown: Boolean = true
) {
    val appColors = AppTheme.colors
    val annotatedString = remember(text, hideMarkdown, appColors) {
        renderMarkdown(text, hideMarkdown, appColors)
    }
    Text(
        text = annotatedString,
        modifier = modifier,
        style = style
    )
}

@Composable
fun RichContentFormattingToolbar(
    state: RichContentEditorState,
    modifier: Modifier = Modifier,
) {
    val isKeyboardOpen = WindowInsets.ime.asPaddingValues().calculateBottomPadding() > 0.dp

    if (state.isFocused && isKeyboardOpen) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            color = AppTheme.colors.surfaceVariant.copy(alpha = 0.95f),
            shadowElevation = 8.dp,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FormattingButton(Icons.Default.FormatBold, state::setBold)
                FormattingButton(Icons.Default.FormatItalic, state::setItalic)
                FormattingButton(Icons.Default.FormatStrikethrough, state::setStrikethrough)
                FormattingButton(Icons.Default.Title, state::toggleHeader)
                FormattingButton(Icons.AutoMirrored.Filled.FormatListBulleted, state::setBulletList)
                FormattingButton(Icons.Default.FormatListNumbered, state::setNumberedList)
            }
        }
    }
}

@Composable
private fun FormattingButton(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(icon, contentDescription = null, tint = AppTheme.colors.onSurfaceVariant)
    }
}

object MarkdownInputTransformation : InputTransformation {
    @OptIn(ExperimentalFoundationApi::class)
    override fun TextFieldBuffer.transformInput() {
        if (changes.changeCount == 1) {
            val range = changes.getRange(0)
            if (range.length == 1 && charAt(range.start) == '\n') {
                val text = toString()
                val cursor = range.start

                val lastLineStart = text.lastIndexOf('\n', cursor - 1).let { if (it == -1) 0 else it + 1 }
                val lastLine = text.substring(lastLineStart, cursor)

                val match = Regex("^([ \\t]*(?:\\d+\\.|-|\\*)[ \\t]+)(.*)$").find(lastLine)
                if (match != null) {
                    val prefix = match.groupValues[1]
                    val content = match.groupValues[2]

                    if (content.trim().isEmpty()) {
                        delete(lastLineStart, cursor)
                    } else {
                        var newPrefix = prefix
                        val numberMatch = Regex("^([ \\t]*)(\\d+)(\\.[ \\t]+)$").find(prefix)
                        if (numberMatch != null) {
                            val indent = numberMatch.groupValues[1]
                            val num = numberMatch.groupValues[2].toIntOrNull() ?: 0
                            val suffix = numberMatch.groupValues[3]
                            newPrefix = "$indent${num + 1}$suffix"
                        }
                        insert(cursor + 1, newPrefix)
                    }
                }
            }
        }
    }
}

class MarkdownOutputTransformation(private val appColors: AppColors) : OutputTransformation {
    override fun TextFieldBuffer.transformOutput() {
        applyMarkdownToBuffer(this, appColors)
    }
}

private fun applyMarkdownToBuffer(buffer: TextFieldBuffer, appColors: AppColors) {
    val textColor = appColors.onSurface
    val mutedColor = appColors.outline
    val bulletColor = appColors.bulletColor

    // 1. Initial bullet replacement (Always needed for the special dot)
    Regex("^[ \\t]*([-*])(?=[ \\t]+)", RegexOption.MULTILINE).findAll(buffer.asCharSequence()).forEach { match ->
        val bullet = match.groups[1]!!
        buffer.replace(bullet.range.first, bullet.range.last + 1, "•")
    }

    val text = buffer.toString()

    // Headers
    Regex("^(#{1,6})([ \\t]*)(.*)$", RegexOption.MULTILINE).findAll(text).forEach { match ->
        val hashes = match.groups[1]!!
        val spacing = match.groups[2]!!
        val content = match.groups[3]!!
        val size = when (hashes.value.length) {
            1 -> 32.sp
            2 -> 28.sp
            3 -> 24.sp
            else -> 20.sp
        }
        val hColor = when (hashes.value.length) {
            1 -> appColors.onSurfaceVariant
            2 -> appColors.onSurfaceVariant
            3 -> appColors.primary
            else -> appColors.onSurfaceVariant
        }
        buffer.addStyle(SpanStyle(color = mutedColor, fontSize = size * 0.6f, fontWeight = FontWeight.Bold), hashes.range.first, hashes.range.last + 1)
        buffer.addStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = size, color = hColor), spacing.range.first, content.range.last + 1)
    }

    // Bullet Dot Styling
    Regex("^[ \\t]*(•)(?=[ \\t]+)", RegexOption.MULTILINE).findAll(text).forEach { match ->
        val bullet = match.groups[1]!!
        buffer.addStyle(
            SpanStyle(
                color = bulletColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                baselineShift = BaselineShift(-0.2f)
            ),
            bullet.range.first,
            bullet.range.last + 1
        )
    }

    // Numbered List Styling
    Regex("^[ \\t]*(\\d+\\.)(?=[ \\t]+)", RegexOption.MULTILINE).findAll(text).forEach { match ->
        val number = match.groups[1]!!
        buffer.addStyle(
            SpanStyle(
                color = bulletColor,
                fontWeight = FontWeight.Bold
            ),
            number.range.first,
            number.range.last + 1
        )
    }

    val markerStyle = SpanStyle(color = mutedColor, fontSize = 12.sp, baselineShift = BaselineShift.Superscript)

    // Bold
    Regex("\\*\\*(.*?)\\*\\*").findAll(text).forEach { match ->
        val startMarker = match.range.first
        val endMarker = match.range.last - 1
        buffer.addStyle(SpanStyle(fontWeight = FontWeight.Bold, color = textColor), match.range.first + 2, match.range.last - 1)
        buffer.addStyle(markerStyle, startMarker, startMarker + 2)
        buffer.addStyle(markerStyle, endMarker, endMarker + 2)
    }

    // Italics
    Regex("/(.*?)/").findAll(text).forEach { match ->
        val startMarker = match.range.first
        val endMarker = match.range.last
        buffer.addStyle(SpanStyle(fontStyle = FontStyle.Italic, color = textColor), match.range.first + 1, match.range.last)
        buffer.addStyle(markerStyle, startMarker, startMarker + 1)
        buffer.addStyle(markerStyle, endMarker, endMarker + 1)
    }

    // Strikethrough
    Regex("~~(.*?)~~").findAll(text).forEach { match ->
        val startMarker = match.range.first
        val endMarker = match.range.last - 1
        buffer.addStyle(SpanStyle(textDecoration = TextDecoration.LineThrough, color = textColor), match.range.first + 2, match.range.last - 1)
        buffer.addStyle(markerStyle, startMarker, startMarker + 2)
        buffer.addStyle(markerStyle, endMarker, endMarker + 2)
    }
}

private fun renderMarkdown(text: String, hideMarkdown: Boolean, appColors: AppColors): AnnotatedString {
    // 1. Replace bullets (1-to-1)
    val processedText = text.replace(Regex("(^[ \\t]*)([-*])(?=[ \\t]+)", RegexOption.MULTILINE)) { match ->
        match.groups[1]!!.value + "•"
    }

    return buildAnnotatedString {
        append(processedText)

        val textColor = appColors.onSurface
        val mutedColor = appColors.outline
        val bulletColor = appColors.bulletColor
        val hiddenStyle = SpanStyle(color = Color.Transparent, fontSize = 0.sp)
        val markerStyle = SpanStyle(color = mutedColor, fontSize = 12.sp, baselineShift = BaselineShift.Superscript)

        // Headers
        Regex("^(#{1,6})([ \\t]*)(.*)$", RegexOption.MULTILINE).findAll(processedText).forEach { match ->
            val hashes = match.groups[1]!!
            val spacing = match.groups[2]!!
            val content = match.groups[3]!!
            val size = if (hideMarkdown) {
                16.sp // Normal font size when markdown is hidden
            } else {
                when (hashes.value.length) {
                    1 -> 32.sp
                    2 -> 28.sp
                    3 -> 24.sp
                    else -> 20.sp
                }
            }
            val hColor = when (hashes.value.length) {
                1 -> appColors.onSurfaceVariant
                2 -> appColors.onSurfaceVariant
                3 -> appColors.primary
                else -> appColors.onSurfaceVariant
            }
            if (hideMarkdown) {
                addStyle(hiddenStyle, hashes.range.first, spacing.range.last + 1)
                addStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = size, color = hColor), content.range.first, content.range.last + 1)
            } else {
                addStyle(SpanStyle(color = mutedColor, fontSize = size * 0.6f, fontWeight = FontWeight.Bold), hashes.range.first, hashes.range.last + 1)
                addStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = size, color = hColor), spacing.range.first, content.range.last + 1)
            }
        }

        // Bullet Dot Styling
        Regex("^[ \\t]*(•)(?=[ \\t]+)", RegexOption.MULTILINE).findAll(processedText).forEach { match ->
            val bullet = match.groups[1]!!
            addStyle(
                SpanStyle(
                    color = bulletColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    baselineShift = BaselineShift(-0.2f)
                ),
                bullet.range.first,
                bullet.range.last + 1
            )
        }

        // Numbered List Styling
        Regex("^[ \\t]*(\\d+\\.)(?=[ \\t]+)", RegexOption.MULTILINE).findAll(processedText).forEach { match ->
            val number = match.groups[1]!!
            addStyle(
                SpanStyle(
                    color = bulletColor,
                    fontWeight = FontWeight.Bold
                ),
                number.range.first,
                number.range.last + 1
            )
        }

        // Bold
        Regex("\\*\\*(.*?)\\*\\*").findAll(processedText).forEach { match ->
            val startMarker = match.range.first
            val endMarker = match.range.last - 1
            addStyle(SpanStyle(fontWeight = FontWeight.Bold, color = textColor), match.range.first + 2, match.range.last - 1)
            if (hideMarkdown) {
                addStyle(hiddenStyle, startMarker, startMarker + 2)
                addStyle(hiddenStyle, endMarker, endMarker + 2)
            } else {
                addStyle(markerStyle, startMarker, startMarker + 2)
                addStyle(markerStyle, endMarker, endMarker + 2)
            }
        }

        // Italics
        Regex("/(.*?)/").findAll(processedText).forEach { match ->
            val startMarker = match.range.first
            val endMarker = match.range.last
            addStyle(SpanStyle(fontStyle = FontStyle.Italic, color = textColor), match.range.first + 1, match.range.last)
            if (hideMarkdown) {
                addStyle(hiddenStyle, startMarker, startMarker + 1)
                addStyle(hiddenStyle, endMarker, endMarker + 1)
            } else {
                addStyle(markerStyle, startMarker, startMarker + 1)
                addStyle(markerStyle, endMarker, endMarker + 1)
            }
        }

        // Strikethrough
        Regex("~~(.*?)~~").findAll(processedText).forEach { match ->
            val startMarker = match.range.first
            val endMarker = match.range.last - 1
            addStyle(SpanStyle(textDecoration = TextDecoration.LineThrough, color = textColor), match.range.first + 2, match.range.last - 1)
            if (hideMarkdown) {
                addStyle(hiddenStyle, startMarker, startMarker + 2)
                addStyle(hiddenStyle, endMarker, endMarker + 2)
            } else {
                addStyle(markerStyle, startMarker, startMarker + 2)
                addStyle(markerStyle, endMarker, endMarker + 2)
            }
        }
    }
}

// PREVIEWS

@Preview(showBackground = true)
@Composable
fun PreviewInteractiveEditorx() = AppTheme {
    val content = """
        # Header 1
        ## Header 2
        ### Header 3
        
        **Bold text** and /Italic text/
        ~~Strikethrough text~~
        
        - Bullet point 1
        - Bullet point 2
          - Nested (manual indent)
        
        1. Numbered list
        2. Second item
        
        **Bold and /Italic/ combined**
    """.trimIndent()

    val state = rememberRichContentEditorState(content)

    Scaffold(
        content = {
            Column(
                modifier = Modifier
                    .safeDrawingPadding()
                    .padding(it)
                    .padding(8.dp)
            ) {
                RichContentEditor(state = state, modifier = Modifier.weight(1f))
                RichContentFormattingToolbar(state = state)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewRenderMode() = AppTheme {
    val content = """
        # Header 1
        **Bold text** and /Italic text/
        ~~Strikethrough text~~
        - Bullet 1
        - Bullet 2
        1. Numbered item
    """.trimIndent()
    val state = rememberRichContentEditorState(content)
    Column(Modifier.padding(16.dp)) {
        Text("Render Mode (Markdown hidden):", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        RichContentEditor(state = state, isReadOnly = true)
    }
}

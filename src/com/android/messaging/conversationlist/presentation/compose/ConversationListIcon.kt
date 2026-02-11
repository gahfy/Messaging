package com.android.messaging.conversationlist.presentation.compose

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.android.messaging.conversationlist.presentation.ConversationListState.Loaded.ConversationRow.Icon
import com.android.messaging.presentation.theme.LocalAppColors

@Composable
internal fun ConversationListIcon(
    modifier: Modifier = Modifier,
    icon: Icon
) {
    when (icon) {
        is Icon.Initials -> ConversationListIconInitial(
            modifier = modifier,
            initial = icon.initials,
            tint = getTint(icon.tintIndex)
        )

        is Icon.Media -> ConversationListIconMedia(
            modifier = modifier,
            mediaUri = icon.mediaUri
        )

        is Icon.Group -> ConversationListIconGroup(
            modifier = modifier,
            icons = icon.icons
        )

        is Icon.Default -> ConversationListIconDefault(
            modifier = modifier,
            tint = getTint(icon.tintIndex)
        )
    }
}

@Composable
private fun ConversationListIconGroup(
    modifier: Modifier,
    icons: List<Icon>
) {
    when (icons.size) {
        0 -> ConversationListIconDefault(
            modifier = modifier
        )

        1 -> ConversationListIcon(
            modifier = modifier,
            icon = icons[0]
        )

        2 -> ConversationListIconGroupDouble(
            modifier = modifier,
            icons = icons
        )

        3 -> ConversationListIconGroupTriple(
            modifier = modifier,
            icons = icons
        )

        else -> ConversationListIconGroupQuadruple(
            modifier = modifier,
            icons = icons
        )
    }
}

@Composable
private fun ConversationListIconDefault(
    modifier: Modifier,
    tint: Color = Color(0xFF4E79A7)
) {
    var sizeDp by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .onSizeChanged {
                sizeDp = with(density) { it.width.toDp() }
            }
            .clip(RoundedCornerShape(sizeDp.div(2)))
    ) {
        Icon(
            modifier = Modifier
                .wrapContentSize(unbounded = true)
                .size(sizeDp.times(1.25f)),
            imageVector = Icons.Default.AccountCircle,
            tint = tint,
            contentDescription = ""
        )
    }
}

@Composable
private fun ConversationListIconMedia(
    modifier: Modifier,
    mediaUri: Uri
) {
    var sizeDp by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    AsyncImage(
        modifier = modifier
            .onSizeChanged { sizeDp = with(density) { it.width.toDp() } }
            .clip(RoundedCornerShape(sizeDp.div(2))),
        contentScale = ContentScale.Crop,
        model = mediaUri,
        contentDescription = ""
    )
}

@Composable
private fun ConversationListIconInitial(
    initial: String,
    modifier: Modifier,
    tint: Color
) {
    var sizeDp by remember { mutableStateOf(0.dp) }
    var sizeSp by remember { mutableStateOf(0.sp) }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .onSizeChanged {
                sizeDp = with(density) { it.width.toDp() }
                sizeSp = with(density) { it.width.toSp() }
            }
            .clip(RoundedCornerShape(sizeDp.div(2)))
            .background(tint)
    ) {
        GlyphPerfectCenteredLetter(
            modifier = Modifier.fillMaxSize(),
            letter = initial,
            style = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = sizeSp.times(0.7f),
                color = LocalAppColors.current.background
            )
        )
    }
}

@Composable
private fun ConversationListIconGroupDouble(
    modifier: Modifier,
    icons: List<Icon>
) {
    var sizeDp by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { sizeDp = with(density) { it.width.toDp() } }
    ) {
        ConversationListIcon(
            modifier = Modifier
                .size(sizeDp.times(0.59f))
                .align(Alignment.TopStart),
            icon = icons[0]
        )
        ConversationListIcon(
            modifier = Modifier
                .size(sizeDp.times(0.59f))
                .align(Alignment.BottomEnd),
            icon = icons[1]
        )
    }
}

@Composable
private fun ConversationListIconGroupTriple(
    modifier: Modifier,
    icons: List<Icon>
) {
    var sizeDp by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { sizeDp = with(density) { it.width.toDp() } }
    ) {
        ConversationListIcon(
            modifier = Modifier
                .size(sizeDp.times(0.5f))
                .offset(y = sizeDp.times(0.033f))
                .align(Alignment.TopCenter),
            icon = icons[0]
        )
        ConversationListIcon(
            modifier = Modifier
                .size(sizeDp.times(0.5f))
                .offset(y = sizeDp.times(-0.033f))
                .align(Alignment.BottomStart),
            icon = icons[1]
        )
        ConversationListIcon(
            modifier = Modifier
                .size(sizeDp.times(0.5f))
                .offset(y = sizeDp.times(-0.033f))
                .align(Alignment.BottomEnd),
            icon = icons[2]
        )
    }
}

@Composable
private fun ConversationListIconGroupQuadruple(
    modifier: Modifier,
    icons: List<Icon>
) {
    var sizeDp by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { sizeDp = with(density) { it.width.toDp() } }
    ) {
        ConversationListIcon(
            modifier = Modifier
                .size(sizeDp.times(0.5f))
                .align(Alignment.TopStart),
            icon = icons[0]
        )
        ConversationListIcon(
            modifier = Modifier
                .size(sizeDp.times(0.5f))
                .align(Alignment.TopEnd),
            icon = icons[1]
        )
        ConversationListIcon(
            modifier = Modifier
                .size(sizeDp.times(0.5f))
                .align(Alignment.BottomStart),
            icon = icons[2]
        )
        ConversationListIcon(
            modifier = Modifier
                .size(sizeDp.times(0.5f))
                .align(Alignment.BottomEnd),
            icon = icons[3]
        )
    }
}

@Composable
private fun getTint(tintIndex: Int): Color {
    return when (tintIndex) {
        0 -> LocalAppColors.current.contact1
        1 -> LocalAppColors.current.contact2
        2 -> LocalAppColors.current.contact3
        3 -> LocalAppColors.current.contact4
        4 -> LocalAppColors.current.contact5
        5 -> LocalAppColors.current.contact6
        else -> LocalAppColors.current.contact7
    }
}

/**
 * Draws a letter with perfect bounds to allow centering it in the icon
 */
@Composable
private fun GlyphPerfectCenteredLetter(
    letter: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle(

    )
) {
    val measurer = rememberTextMeasurer()

    val layoutResult = remember(letter, style) {
        measurer.measure(
            text = AnnotatedString(letter),
            style = style,
            maxLines = 1
        )
    }

    val bounds = layoutResult.getPathForRange(0, letter.length).getBounds()

    val glyphCx = bounds.left + bounds.width / 2f
    val glyphCy = bounds.top + bounds.height / 2f

    val layoutCx = layoutResult.size.width / 2f
    val layoutCy = layoutResult.size.height / 2f

    val dxPx = layoutCx - glyphCx
    val dyPx = layoutCy - glyphCy

    Box(modifier, contentAlignment = Alignment.Center) {
        Text(
            text = letter,
            style = style,
            modifier = Modifier.offset {
                IntOffset(dxPx.toInt(), dyPx.toInt())
            }
        )
    }
}

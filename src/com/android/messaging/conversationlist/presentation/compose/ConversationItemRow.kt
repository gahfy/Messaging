package com.android.messaging.conversationlist.presentation.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.android.messaging.conversationlist.presentation.ConversationListState
import com.android.messaging.presentation.theme.LocalAppColors
import com.android.messaging.presentation.theme.LocalDimensions

@Composable
internal fun ConversationItemRow(
    state: ConversationListState.Loaded,
    conversation: ConversationListState.Loaded.ConversationRow,
    content: @Composable RowScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .background(LocalAppColors.current.background)
            .padding(
                horizontal = LocalDimensions.current.mMargin,
                vertical = LocalDimensions.current.xxsMargin
            )
    ) {
        var heightDp by remember { mutableStateOf(0.dp) }
        val density = LocalDensity.current

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { heightDp = with(density) { it.height.toDp() } }
                .clip(
                    RoundedCornerShape(heightDp.div(2))
                )
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {
                        state.onConversationClicked(conversation)
                    },
                    onLongClick = {
                        state.onConversationLongClicked(conversation)
                    },
                    indication = ripple(
                        bounded = true,
                        color = LocalAppColors.current.onBackgroundSecondary
                    ),
                )
                .then(
                    if (conversation.selected) {
                        Modifier.background(LocalAppColors.current.highlightedBackground)
                    } else {
                        Modifier
                    }
                )
                .padding(
                    horizontal = LocalDimensions.current.sMargin,
                    vertical = LocalDimensions.current.mMargin
                ),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

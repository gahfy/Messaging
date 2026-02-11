package com.android.messaging.conversationlist.presentation.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.android.messaging.conversationlist.presentation.ConversationListState
import com.android.messaging.presentation.theme.LocalAppColors
import com.android.messaging.presentation.theme.LocalDimensions

@Composable
internal fun ConversationItemSwipeToDismissBox(
    state: ConversationListState.Loaded,
    conversation: ConversationListState.Loaded.ConversationRow,
    content: @Composable RowScope.() -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    var initialized by remember { mutableStateOf(false) }

    val positionalThreshold = SwipeToDismissBoxDefaults.positionalThreshold

    val swipeState = remember(conversation.id) {
        SwipeToDismissBoxState(
            SwipeToDismissBoxValue.Settled,
            positionalThreshold
        )
    }

    LaunchedEffect(swipeState.currentValue) {
        when (swipeState.currentValue) {
            SwipeToDismissBoxValue.EndToStart -> {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }

            SwipeToDismissBoxValue.StartToEnd -> {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }

            else -> {
                if (initialized) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                } else {
                    initialized = true
                }
            }
        }
    }
    SwipeToDismissBox(
        state = swipeState,
        enableDismissFromEndToStart = state.swipeEnabled,
        enableDismissFromStartToEnd = state.swipeEnabled,
        onDismiss = {
            state.onConversationSwiped(conversation)
        },
        backgroundContent = {
            DeleteBackground(swipeState)
        },
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteBackground(state: SwipeToDismissBoxState) {
    Row(
        modifier = Modifier
            .background(LocalAppColors.current.statusBackground)
            .fillMaxSize()
            .padding(horizontal = LocalDimensions.current.mMargin),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (state.currentValue == SwipeToDismissBoxValue.StartToEnd || state.currentValue == SwipeToDismissBoxValue.Settled) {
            Icon(
                imageVector = Icons.Outlined.Archive,
                contentDescription = null,
                tint = LocalAppColors.current.statusForeground
            )
        }
        Spacer(modifier = Modifier.weight(1.0f))
        if (state.currentValue == SwipeToDismissBoxValue.EndToStart || state.currentValue == SwipeToDismissBoxValue.Settled) {
            Icon(
                imageVector = Icons.Outlined.Archive,
                contentDescription = null,
                tint = LocalAppColors.current.statusForeground
            )
        }
    }
}

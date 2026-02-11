package com.android.messaging.conversationlist.presentation.compose

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.android.messaging.R
import com.android.messaging.conversationlist.presentation.ConversationListEvent
import com.android.messaging.conversationlist.presentation.ConversationListState
import com.android.messaging.presentation.theme.LocalAppColors
import com.android.messaging.presentation.theme.LocalDimensions

@Composable
internal fun ConversationListSnackbarHost(
    snackbarHostState: SnackbarHostState,
) {
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier
            .navigationBarsPadding()
            .padding(
                bottom = LocalDimensions.current.xlMargin,
                start = LocalDimensions.current.mMargin,
                end = LocalDimensions.current.mMargin
            ),
    ) { data ->
        Snackbar(
            snackbarData = data,
            shape = RoundedCornerShape(LocalDimensions.current.mCornerRadius),
            containerColor = LocalAppColors.current.revertBackground,
            contentColor = LocalAppColors.current.onRevertBackground,
            actionColor = LocalAppColors.current.onRevertBackgroundAction
        )
    }
}

/**
 * This Composable will handle the snackbar which should be shown when archive actions are
 * performed.
 */
@Composable
internal fun SnackbarHandler(
    snackbarHostState: SnackbarHostState,
    state: ConversationListState
) {
    val events = (state as? ConversationListState.Loaded)?.events

    val archivedMessage = stringResource(R.string.archived_toast_message)
    val unarchivedMessage = stringResource(R.string.unarchived_toast_message)
    val archivedActionLabel = stringResource(R.string.action_unarchive)

    LaunchedEffect(events) {
        events?.collect { event ->
            when (event) {
                is ConversationListEvent.ConversationArchived -> {
                    val result = snackbarHostState.showSnackbar(
                        message = String.format(archivedMessage, event.conversationIds.size),
                        actionLabel = archivedActionLabel,
                        withDismissAction = false,
                        duration = SnackbarDuration.Long
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        state.onConversationsUnarchived(event.conversationIds)
                    }
                }

                is ConversationListEvent.ConversationUnarchived -> {
                    snackbarHostState.showSnackbar(
                        message = String.format(unarchivedMessage, event.count),
                        withDismissAction = false,
                        duration = SnackbarDuration.Short
                    )
                }

                else -> {}
            }
        }
    }
}

package com.android.messaging.conversationlist.presentation.compose

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.android.messaging.R
import com.android.messaging.conversationlist.presentation.ConversationListState
import com.android.messaging.presentation.theme.LocalAppColors
import com.android.messaging.presentation.theme.LocalDimensions

@Composable
internal fun BoxScope.ConversationListFAB(
    state: ConversationListState.Loaded,
    isScrollingDown: Boolean
) {
    FloatingActionButton(
        modifier = Modifier
            .padding(
                all = LocalDimensions.current.mMargin
            )
            .navigationBarsPadding()
            .align(Alignment.BottomEnd)
            .animateContentSize(),
        onClick = state.onNewConversationClicked,
        containerColor = LocalAppColors.current.fabBackground,
        contentColor = LocalAppColors.current.fabForeground
    ) {
        Row(
            modifier = Modifier.padding(horizontal = LocalDimensions.current.mMargin)
        ) {
            Icon(
                imageVector = Icons.Default.ChatBubbleOutline,
                contentDescription = stringResource(R.string.start_new_conversation)
            )
            if (!isScrollingDown) {
                Spacer(modifier = Modifier.width(LocalDimensions.current.sMargin))
                Text(stringResource(R.string.start_new_conversation))
            }
        }
    }
}

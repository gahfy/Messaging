package com.android.messaging.conversationlist.presentation.compose

import android.content.res.Configuration
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.android.messaging.R
import com.android.messaging.conversationlist.presentation.ConversationListState
import com.android.messaging.presentation.theme.AppTheme
import com.android.messaging.presentation.theme.LocalAppColors
import com.android.messaging.presentation.theme.LocalDimensions

@Composable
internal fun ConversationItemIcon(
    conversation: ConversationListState.Loaded.ConversationRow
) {
    Box {
        androidx.compose.animation.AnimatedVisibility(
            visible = !conversation.selected,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ConversationListIcon(
                modifier = Modifier
                    .size(dimensionResource(R.dimen.conversation_list_contact_icon_size)),
                icon = conversation.icon
            )
        }
        androidx.compose.animation.AnimatedVisibility(
            visible = conversation.selected,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(modifier = Modifier.size(LocalDimensions.current.bigIconSize)) {
                Icon(
                    modifier = Modifier
                        .wrapContentSize(unbounded = true)
                        .size(LocalDimensions.current.bigIconSize.times(1.2f)),
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = stringResource(R.string.selected),
                    tint = LocalAppColors.current.statusBackground
                )
            }
        }
    }
}

@Preview(
    name = "Light",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true
)
@Preview(
    name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
private fun ConversationItemIconPreviewUnselected() {
    AppTheme {
        Surface {
            ConversationItemIcon(
                conversation = ConversationListState.Loaded.ConversationRow(
                    id = "",
                    timestamp = 0L,
                    icon = ConversationListState.Loaded.ConversationRow.Icon.Initials(
                        initials = "A",
                        tintIndex = 0
                    ),
                    name = "",
                    subject = "",
                    snippetText = "",
                    time = "",
                    bottomMessageResId = 0,
                    hasError = false,
                    isStrong = false,
                    snippetMaxLines = 1,
                    selected = false
                )
            )
        }
    }
}

@Preview(
    name = "Light",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true
)
@Preview(
    name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
private fun ConversationItemIconPreviewSelected() {
    AppTheme {
        Surface {
            ConversationItemIcon(
                conversation = ConversationListState.Loaded.ConversationRow(
                    id = "",
                    timestamp = 0L,
                    icon = ConversationListState.Loaded.ConversationRow.Icon.Initials(
                        initials = "A",
                        tintIndex = 0
                    ),
                    name = "",
                    subject = "",
                    snippetText = "",
                    time = "",
                    bottomMessageResId = 0,
                    hasError = false,
                    isStrong = false,
                    snippetMaxLines = 1,
                    selected = true
                )
            )
        }
    }
}

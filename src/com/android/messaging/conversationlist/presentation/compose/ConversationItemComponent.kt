package com.android.messaging.conversationlist.presentation.compose

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.android.messaging.R
import com.android.messaging.conversationlist.presentation.ConversationListState
import com.android.messaging.presentation.theme.AppTheme
import com.android.messaging.presentation.theme.LocalAppColors
import com.android.messaging.presentation.theme.LocalDimensions
import kotlinx.coroutines.flow.flowOf

@Composable
fun ConversationItemComponent(
    state: ConversationListState.Loaded,
    conversation: ConversationListState.Loaded.ConversationRow
) {
    ConversationItemSwipeToDismissBox(
        state = state,
        conversation = conversation
    ) {
        ConversationItemRow(
            state = state,
            conversation = conversation
        ) {
            ConversationItemIcon(
                conversation = conversation
            )
            Row(
                modifier = Modifier
                    .padding(horizontal = dimensionResource(R.dimen.conversation_list_item_view_padding)),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier
                        .weight(1.0f)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(bottom = LocalDimensions.current.xxsMargin),
                        text = conversation.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            fontWeight = if (conversation.isStrong) FontWeight.Bold else FontWeight.Normal,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = LocalDimensions.current.body,
                            color = LocalAppColors.current.onBackground
                        )
                    )
                    conversation.subject?.let { subject ->
                        Text(
                            modifier = Modifier
                                .padding(bottom = LocalDimensions.current.xxsMargin),
                            text = subject,
                            style = TextStyle(
                                fontWeight = if (conversation.isStrong) FontWeight.Bold else FontWeight.Normal,
                                fontFamily = FontFamily.SansSerif,
                                fontSize = LocalDimensions.current.bodySmall,
                                color = if (conversation.isStrong) LocalAppColors.current.onBackground else LocalAppColors.current.onBackgroundSecondary
                            )
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (conversation.hasError) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = stringResource(R.string.error),
                                tint = LocalAppColors.current.error,
                                modifier = Modifier.size(LocalDimensions.current.iconInTextSize)
                            )
                            Spacer(modifier = Modifier.width(LocalDimensions.current.xxsMargin))
                        }
                        conversation.snippetText?.let { snippet ->
                            Text(
                                text = snippet,
                                maxLines = conversation.snippetMaxLines,
                                overflow = TextOverflow.Ellipsis,
                                style = TextStyle(
                                    fontWeight = if (conversation.isStrong) FontWeight.Bold else FontWeight.Normal,
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = LocalDimensions.current.bodySmall,
                                    color = if (conversation.isStrong) LocalAppColors.current.onBackground else LocalAppColors.current.onBackgroundSecondary
                                )
                            )
                        }
                    }
                    conversation.bottomMessageResId?.let { bottomMessageResId ->
                        Text(
                            text = stringResource(bottomMessageResId),
                            style = TextStyle(
                                fontWeight = if (conversation.isStrong) FontWeight.Bold else FontWeight.Normal,
                                fontFamily = FontFamily.SansSerif,
                                fontSize = LocalDimensions.current.bodySmall,
                                color = if (conversation.isStrong) LocalAppColors.current.onBackground else LocalAppColors.current.onBackgroundSecondary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(LocalDimensions.current.sMargin))
                conversation.time?.let { time ->
                    Text(
                        text = time,
                        style = TextStyle(
                            fontWeight = if (conversation.isStrong) FontWeight.Bold else FontWeight.Normal,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = LocalDimensions.current.bodySmall,
                            color = if (conversation.isStrong) LocalAppColors.current.onBackground else LocalAppColors.current.onBackgroundSecondary
                        )
                    )
                }
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
fun ConversationItemComponentPreview() {
    AppTheme {
        Surface {
            ConversationItemComponent(
                conversation = dummyConversation,
                state = dummyState
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
fun ConversationItemComponentUnreadPreview() {
    AppTheme {
        Surface {
            ConversationItemComponent(
                conversation = dummyConversation.copy(
                    isStrong = true,
                    snippetMaxLines = 3
                ),
                state = dummyState
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
fun ConversationItemComponentSelectedPreview() {
    AppTheme {
        Surface {
            ConversationItemComponent(
                conversation = dummyConversation.copy(
                    selected = true
                ),
                state = dummyState
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
fun ConversationItemComponentWithErrorPreview() {
    AppTheme {
        Surface {
            ConversationItemComponent(
                conversation = dummyConversation.copy(
                    hasError = true
                ),
                state = dummyState
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
fun ConversationItemComponentDraftPreview() {
    AppTheme {
        Surface {
            ConversationItemComponent(
                conversation = dummyConversation.copy(
                    bottomMessageResId = R.string.conversation_list_item_view_draft_message
                ),
                state = dummyState
            )
        }
    }
}

private val dummyConversation = ConversationListState.Loaded.ConversationRow(
    id = "",
    timestamp = 0L,
    icon = ConversationListState.Loaded.ConversationRow.Icon.Initials(
        initials = "A",
        tintIndex = 0
    ),
    name = "Alice",
    subject = null,
    snippetText = "I just tried GrapheneOS and I found it cool. Feel free to try it as well my " +
            "dear Bob. Let's keep in touch and see you soon",
    time = "mon.",
    bottomMessageResId = null,
    hasError = false,
    isStrong = false,
    snippetMaxLines = 1,
    selected = false
)

private val dummyState = ConversationListState.Loaded(
    conversations = listOf(),
    swipeEnabled = false,
    displayActionsMember = false,
    onConversationClicked = {},
    onConversationSwiped = {},
    onConversationsUnarchived = {},
    onNewConversationClicked = {},
    onConversationLongClicked = {},
    onDeleteActionClicked = {},
    onArchiveActionClicked = {},
    onSettingsClickedMember = {},
    onArchivedClickedMember = {},
    events = flowOf()
)

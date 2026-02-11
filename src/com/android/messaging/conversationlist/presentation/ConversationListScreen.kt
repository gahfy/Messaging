package com.android.messaging.conversationlist.presentation

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.android.messaging.R
import com.android.messaging.conversationlist.presentation.compose.ConversationItemComponent
import com.android.messaging.conversationlist.presentation.compose.ConversationListAppBar
import com.android.messaging.conversationlist.presentation.compose.ConversationListFAB
import com.android.messaging.conversationlist.presentation.compose.ConversationListSnackbarHost
import com.android.messaging.conversationlist.presentation.compose.SnackbarHandler
import com.android.messaging.presentation.theme.AppTheme
import com.android.messaging.presentation.theme.LocalAppColors
import com.android.messaging.presentation.utils.StatusBarColorScheme
import com.android.messaging.presentation.utils.rememberIsScrollingDown
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationListScreen(
    state: ConversationListState
) {
    StatusBarColorScheme()

    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        contentWindowInsets = WindowInsets.systemBars
            .exclude(WindowInsets.navigationBars),
        topBar = { ConversationListAppBar(state) },
        snackbarHost = { ConversationListSnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        SnackbarHandler(
            snackbarHostState = snackbarHostState,
            state = state
        )

        Box(
            modifier = Modifier
                .padding(paddingValues)
                .background(LocalAppColors.current.background)
        ) {
            when (state) {
                is ConversationListState.Empty -> ConversationListEmptyScreen(state.type)
                is ConversationListState.Loaded -> ConversationListLoadedScreen(state)
            }
        }
    }
}

@Composable
private fun ConversationListLoadedScreen(
    state: ConversationListState.Loaded
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val columnState = rememberLazyListState()
        val isScrollingDown by rememberIsScrollingDown(columnState)

        LazyColumn(
            state = columnState
        ) {
            items(state.conversations.size) { index ->
                val conversation = state.conversations[index]

                ConversationItemComponent(
                    state = state,
                    conversation = conversation
                )
            }
        }
        ConversationListFAB(
            state = state,
            isScrollingDown = isScrollingDown
        )
    }
}

@Composable
private fun ConversationListEmptyScreen(
    type: ConversationListState.Empty.Type
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val message = when (type) {
            ConversationListState.Empty.Type.LOADING -> stringResource(R.string.conversation_list_first_sync_text)
            ConversationListState.Empty.Type.ARCHIVE_EMPTY -> stringResource(R.string.archived_conversation_list_empty_text)
            ConversationListState.Empty.Type.EMPTY -> stringResource(R.string.conversation_list_empty_text)
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_oobe_conv_list),
                contentDescription = message
            )
            Spacer(
                modifier = Modifier.height(dimensionResource(R.dimen.list_empty_text_top_margin))
            )
            Text(
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.list_empty_text_left_right_margin)),
                text = message,
                color = LocalAppColors.current.onBackground
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
fun ConversationListScreenPreview() {
    AppTheme {
        Surface {
            ConversationListScreen(
                state = ConversationListState.Loaded(
                    conversations = listOf(
                        ConversationListState.Loaded.ConversationRow(
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
                            time = "01:32",
                            bottomMessageResId = null,
                            hasError = false,
                            isStrong = false,
                            snippetMaxLines = 1,
                            selected = false
                        ),

                        ConversationListState.Loaded.ConversationRow(
                            id = "",
                            timestamp = 0L,
                            icon = ConversationListState.Loaded.ConversationRow.Icon.Group(
                                icons = listOf(
                                    ConversationListState.Loaded.ConversationRow.Icon.Initials(
                                        initials = "A",
                                        tintIndex = 0
                                    ),
                                    ConversationListState.Loaded.ConversationRow.Icon.Initials(
                                        initials = "B",
                                        tintIndex = 2
                                    )
                                )
                            ),
                            name = "Bob, Alice",
                            subject = null,
                            snippetText = "Sending a message to myself and Alice to try the features of the SMS app",
                            time = "mon.",
                            bottomMessageResId = null,
                            hasError = false,
                            isStrong = true,
                            snippetMaxLines = 1,
                            selected = false
                        ),

                        ConversationListState.Loaded.ConversationRow(
                            id = "",
                            timestamp = 0L,
                            icon = ConversationListState.Loaded.ConversationRow.Icon.Default(
                                tintIndex = 1
                            ),
                            name = "+33 6 00 00 00 00",
                            subject = null,
                            snippetText = "Hey I just tried to sent you a message, did you receive it?",
                            time = "mon.",
                            bottomMessageResId = null,
                            hasError = false,
                            isStrong = true,
                            snippetMaxLines = 1,
                            selected = false
                        ),

                        ConversationListState.Loaded.ConversationRow(
                            id = "",
                            timestamp = 0L,
                            icon = ConversationListState.Loaded.ConversationRow.Icon.Default(
                                tintIndex = 1
                            ),
                            name = "Charles",
                            subject = null,
                            snippetText = "Why is it not sending",
                            time = "mon.",
                            bottomMessageResId = R.string.conversation_list_item_view_draft_message,
                            hasError = true,
                            isStrong = false,
                            snippetMaxLines = 1,
                            selected = true
                        )
                    ),
                    swipeEnabled = false,
                    displayActionsMember = true,
                    onConversationSwiped = {},
                    onConversationsUnarchived = {},
                    onConversationClicked = {},
                    onSettingsClickedMember = {},
                    onArchivedClickedMember = {},
                    onDeleteActionClicked = {},
                    onNewConversationClicked = {},
                    onConversationLongClicked = {},
                    onArchiveActionClicked = {},
                    events = flowOf()
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
fun ConversationListScreenEmptyPreview() {
    AppTheme {
        Surface {
            ConversationListScreen(
                state = ConversationListState.Empty(
                    type = ConversationListState.Empty.Type.EMPTY,
                    onArchivedClickedMember = {},
                    onSettingsClickedMember = {}
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
fun ConversationListScreenLoadingPreview() {
    AppTheme {
        Surface {
            ConversationListScreen(
                state = ConversationListState.Empty(
                    type = ConversationListState.Empty.Type.LOADING,
                    onArchivedClickedMember = {},
                    onSettingsClickedMember = {}
                )
            )
        }
    }
}

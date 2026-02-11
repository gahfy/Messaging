package com.android.messaging.conversationlist.presentation.compose

import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.android.messaging.R
import com.android.messaging.conversationlist.presentation.ConversationListState
import com.android.messaging.presentation.theme.AppTheme
import com.android.messaging.presentation.theme.LocalAppColors
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ConversationListAppBar(
    state: ConversationListState
) {
    TopAppBar(
        colors = TopAppBarColors(
            containerColor = LocalAppColors.current.appBarColor,
            scrolledContainerColor = LocalAppColors.current.appBarColor,
            navigationIconContentColor = LocalAppColors.current.appBarForeground,
            titleContentColor = LocalAppColors.current.appBarForeground,
            actionIconContentColor = LocalAppColors.current.appBarForeground,
            subtitleContentColor = LocalAppColors.current.appBarForeground
        ),
        title = {
            Text(stringResource(R.string.app_name))
        },
        actions = {
            if (state.displayActions) {
                IconButton(onClick = {
                    (state as? ConversationListState.Loaded)?.onDeleteActionClicked()
                }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.action_delete)
                    )
                }
                IconButton(onClick = {
                    (state as? ConversationListState.Loaded)?.onArchiveActionClicked()
                }) {
                    Icon(
                        Icons.Default.Archive,
                        contentDescription = stringResource(R.string.action_archive)
                    )
                }
            }
            MoreMenu(
                onArchivedClicked = state.onArchivedClicked,
                onSettingsClicked = state.onSettingsClicked
            )
        }
    )
}

@Composable
private fun MoreMenu(
    onArchivedClicked: () -> Unit,
    onSettingsClicked: () -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = stringResource(R.string.more)
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.action_menu_show_archived)) },
            onClick = {
                onArchivedClicked()
                expanded = false
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.action_settings)) },
            onClick = {
                onSettingsClicked()
                expanded = false
            }
        )
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
private fun ConversationListAppBarPreview() {
    AppTheme {
        Surface {
            ConversationListAppBar(
                state = ConversationListState.Loaded(
                    conversations = listOf(),
                    swipeEnabled = true,
                    displayActionsMember = true,
                    onNewConversationClicked = {},
                    onConversationLongClicked = {},
                    onConversationSwiped = {},
                    onConversationsUnarchived = {},
                    onArchivedClickedMember = {},
                    onSettingsClickedMember = {},
                    onConversationClicked = {},
                    onDeleteActionClicked = {},
                    onArchiveActionClicked = {},
                    events = flowOf()
                )
            )
        }
    }
}

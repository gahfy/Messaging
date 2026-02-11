package com.android.messaging.conversationlist.presentation

import android.net.Uri
import androidx.core.net.toUri
import com.android.messaging.R
import com.android.messaging.conversationlist.domain.model.ConversationListAvatar
import com.android.messaging.conversationlist.domain.model.ConversationListItem
import com.android.messaging.util.Dates
import kotlinx.coroutines.flow.Flow

sealed class ConversationListState(
    val onArchivedClicked: () -> Unit,
    val onSettingsClicked: () -> Unit,
    val displayActions: Boolean
) {
    data class Empty(
        val type: Type,
        val onArchivedClickedMember: () -> Unit,
        val onSettingsClickedMember: () -> Unit,
    ) : ConversationListState(
        onArchivedClicked = onArchivedClickedMember,
        onSettingsClicked = onSettingsClickedMember,
        displayActions = false
    ) {
        enum class Type {
            LOADING,
            ARCHIVE_EMPTY,
            EMPTY
        }
    }

    data class Loaded(
        val conversations: List<ConversationRow>,
        val swipeEnabled: Boolean,
        val displayActionsMember: Boolean,
        val onConversationClicked: (ConversationRow) -> Unit,
        val onConversationLongClicked: (ConversationRow) -> Unit,
        val onConversationSwiped: (ConversationRow) -> Unit,
        val onConversationsUnarchived: (List<String>) -> Unit,
        val onArchivedClickedMember: () -> Unit,
        val onSettingsClickedMember: () -> Unit,
        val onDeleteActionClicked: () -> Unit,
        val onArchiveActionClicked: () -> Unit,
        val onNewConversationClicked: () -> Unit,
        val events: Flow<ConversationListEvent>
    ) : ConversationListState(
        onArchivedClicked = onArchivedClickedMember,
        onSettingsClicked = onSettingsClickedMember,
        displayActions = displayActionsMember
    ) {
        data class ConversationRow(
            val id: String,
            val timestamp: Long,
            val icon: Icon,
            val name: String,
            val subject: String?,
            val snippetText: String?,
            val time: String?,
            val bottomMessageResId: Int?,
            val hasError: Boolean,
            val isStrong: Boolean,
            val snippetMaxLines: Int,
            val selected: Boolean
        ) {
            sealed interface Icon {
                data class Default(val tintIndex: Int) : Icon
                data class Initials(val initials: String, val tintIndex: Int) : Icon
                data class Media(val mediaUri: Uri) : Icon
                data class Group(val icons: List<Icon>) : Icon
            }
        }
    }
}

fun List<ConversationListItem>.toConversationListStateLoaded(
    onConversationClicked: (ConversationListState.Loaded.ConversationRow) -> Unit,
    onConversationLongClicked: (ConversationListState.Loaded.ConversationRow) -> Unit,
    onConversationSwiped: (ConversationListState.Loaded.ConversationRow) -> Unit,
    onConversationsUnarchived: (List<String>) -> Unit,
    onArchivedClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    onDeleteActionClicked: () -> Unit,
    onArchiveActionClicked: () -> Unit,
    onNewConversationClicked: () -> Unit,
    swipeEnabled: Boolean,
    displayActions: Boolean,
    events: Flow<ConversationListEvent>
): ConversationListState.Loaded {
    return ConversationListState.Loaded(
        conversations = this.map {
            it.toConversationRow()
        },
        swipeEnabled = swipeEnabled,
        displayActionsMember = displayActions,
        onConversationClicked = onConversationClicked,
        onConversationLongClicked = onConversationLongClicked,
        onConversationSwiped = onConversationSwiped,
        onConversationsUnarchived = onConversationsUnarchived,
        onArchivedClickedMember = onArchivedClicked,
        onSettingsClickedMember = onSettingsClicked,
        onDeleteActionClicked = onDeleteActionClicked,
        onArchiveActionClicked = onArchiveActionClicked,
        onNewConversationClicked = onNewConversationClicked,
        events = events
    )
}

fun ConversationListItem.toConversationRow(): ConversationListState.Loaded.ConversationRow {
    return ConversationListState.Loaded.ConversationRow(
        id = conversationId,
        timestamp = sortTimestamp,
        icon = avatar.toConversationIcon(),
        name = name,
        subject = subject?.takeIf { it.isNotEmpty() },
        snippetText = snippetText.takeIf { it.isNotEmpty() },
        time = Dates.getConversationTimeString(
            sortTimestamp
        ).toString(),
        bottomMessageResId = getBottomMessageResId(this),
        hasError = error != null,
        isStrong = !read,
        snippetMaxLines = if (read) 1 else 3,
        selected = false
    )
}

fun getBottomMessageResId(conversationListItem: ConversationListItem): Int? =
    if (conversationListItem.isDraft) {
        R.string.conversation_list_item_view_draft_message
    } else if (conversationListItem.isSending) {
        R.string.message_status_sending
    } else {
        null
    }

fun ConversationListAvatar.toConversationIcon(): ConversationListState.Loaded.ConversationRow.Icon =
    when (this) {
        is ConversationListAvatar.Default -> {
            ConversationListState.Loaded.ConversationRow.Icon.Default(
                tintIndex = getIconTintIndex(name)
            )
        }

        is ConversationListAvatar.Initials -> {
            ConversationListState.Loaded.ConversationRow.Icon.Initials(
                tintIndex = getIconTintIndex(name),
                initials = initials
            )
        }

        is ConversationListAvatar.Media -> {
            ConversationListState.Loaded.ConversationRow.Icon.Media(
                mediaUri = mediaUri.toUri()
            )
        }

        is ConversationListAvatar.Group -> {
            ConversationListState.Loaded.ConversationRow.Icon.Group(
                icons = icons.map {
                    it.toConversationIcon()
                }
            )
        }
    }

private fun getIconTintIndex(name: String): Int {
    return (name.hashCode() and 0x7fffffff) % 7
}

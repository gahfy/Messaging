package com.android.messaging.conversationlist.data

import android.database.Cursor
import androidx.core.net.toUri
import com.android.messaging.conversationlist.domain.model.ConversationListAvatar
import com.android.messaging.conversationlist.domain.model.ConversationListItem
import com.android.messaging.datamodel.data.MessageData
import com.android.messaging.mmslib.pdu.PduHeaders
import com.android.messaging.util.NotificationChannelUtil
import com.android.messaging.util.PhoneUtils

fun Cursor.toConversationListItem(
    ignoreDraft: Boolean
): ConversationListItem {
    val conversationId = getString(COLUMN_INDEX_ID)
    val notificationChannel = NotificationChannelUtil.getConversationChannel(conversationId)
    val messageStatus = getInt(COLUMN_INDEX_MESSAGE_STATUS)
    val rawStatus = getInt(COLUMN_INDEX_RAW_STATUS)
    val showDraft = if (ignoreDraft) false else (getInt(COLUMN_INDEX_SHOW_DRAFT) == 1)
    return ConversationListItem(
        conversationId = conversationId,
        name = getString(COLUMN_INDEX_NAME),
        avatar = getConversationListIcon(getString(COLUMN_INDEX_ICON)),
        read = getInt(COLUMN_INDEX_READ) == 1,
        sortTimestamp = getLong(COLUMN_INDEX_SORT_TIMESTAMP),
        snippetText = getString(COLUMN_INDEX_SNIPPET_TEXT),
        previewUri = getString(COLUMN_INDEX_PREVIEW_URI),
        previewContentType = getString(COLUMN_INDEX_PREVIEW_CONTENT_TYPE),
        participantContactId = getLong(COLUMN_INDEX_PARTICIPANT_CONTACT_ID),
        participantLookupKey = getString(COLUMN_INDEX_PARTICIPANT_LOOKUP_KEY),
        participantNormalizedDestination = getString(
            COLUMN_INDEX_PARTICIPANT_NORMALIZED_DESTINATION
        ),
        currentSelfId = getString(COLUMN_INDEX_CURRENT_SELF_ID),
        participantCount = getInt(COLUMN_INDEX_PARTICIPANT_COUNT),
        notificationEnabled = notificationChannel?.let {
            it.importance > 0
        } ?: (getInt(COLUMN_INDEX_NOTIFICATION_ENABLED) == 1),
        notificationSoundUri = getString(COLUMN_INDEX_NOTIFICATION_SOUND_URI),
        notificationVibrate = notificationChannel?.shouldVibrate() ?: (getInt(
            COLUMN_INDEX_NOTIFICATION_VIBRATION
        ) == 1),
        includeEmailAddress = getInt(COLUMN_INDEX_INCLUDE_EMAIL_ADDR) == 1,
        messageStatus = messageStatus,
        rawStatus = rawStatus,
        error = if (hasFailureMessage(messageStatus)) {
            if (isMessageTypeOutgoing(messageStatus)) {
                getOutgoingErrorType(rawStatus)
            } else {
                ConversationListItem.Error.DOWNLOAD
            }
        } else {
            null
        },
        isDraft = isDraft(showDraft, messageStatus),
        isSending = isSending(messageStatus),
        showDraft = showDraft,
        draftPreviewUri = if (ignoreDraft) null else getString(COLUMN_INDEX_DRAFT_PREVIEW_URI),
        draftPreviewContentType = if (ignoreDraft) null else getString(
            COLUMN_INDEX_DRAFT_PREVIEW_CONTENT_TYPE
        ),
        draftSnippetText = if (ignoreDraft) null else getString(COLUMN_INDEX_DRAFT_SNIPPET_TEXT),
        archived = getInt(COLUMN_INDEX_ARCHIVE_STATUS) == 1,
        subject = getString(COLUMN_INDEX_SUBJECT_TEXT),
        draftSubject = if (ignoreDraft) null else getString(COLUMN_INDEX_DRAFT_SUBJECT_TEXT),
        snippetSenderFirstName = getString(COLUMN_INDEX_SNIPPET_SENDER_FIRST_NAME),
        snippetSenderDisplayDestination = getString(COLUMN_INDEX_SNIPPET_SENDER_DISPLAY_DESTINATION),
        isEnterprise = getInt(COLUMN_INDEX_IS_ENTERPRISE) == 1
    )
}

private fun hasFailureMessage(messageStatus: Int): Boolean = (
        messageStatus == MessageData.BUGLE_STATUS_OUTGOING_FAILED ||
                messageStatus == MessageData.BUGLE_STATUS_OUTGOING_FAILED_EMERGENCY_NUMBER ||
                messageStatus == MessageData.BUGLE_STATUS_INCOMING_DOWNLOAD_FAILED ||
                messageStatus == MessageData.BUGLE_STATUS_INCOMING_EXPIRED_OR_NOT_AVAILABLE &&
                PhoneUtils.getDefault().isDefaultSmsApp()
        )

private fun isMessageTypeOutgoing(messageStatus: Int): Boolean = (
        messageStatus < MessageData.BUGLE_STATUS_FIRST_INCOMING
        )

private fun getOutgoingErrorType(rawStatus: Int): ConversationListItem.Error =
    when (rawStatus) {
        PduHeaders.RESPONSE_STATUS_ERROR_SERVICE_DENIED,
        PduHeaders.RESPONSE_STATUS_ERROR_PERMANENT_SERVICE_DENIED
            -> ConversationListItem.Error.OUTGOING_SERVICE
        PduHeaders.RESPONSE_STATUS_ERROR_SENDING_ADDRESS_UNRESOLVED,
        PduHeaders.RESPONSE_STATUS_ERROR_TRANSIENT_SENDNG_ADDRESS_UNRESOLVED,
        PduHeaders.RESPONSE_STATUS_ERROR_PERMANENT_SENDING_ADDRESS_UNRESOLVED
            -> ConversationListItem.Error.OUTGOING_ADDRESS
        PduHeaders.RESPONSE_STATUS_ERROR_MESSAGE_FORMAT_CORRUPT,
        PduHeaders.RESPONSE_STATUS_ERROR_PERMANENT_MESSAGE_FORMAT_CORRUPT
            -> ConversationListItem.Error.OUTGOING_CORRUPT
        PduHeaders.RESPONSE_STATUS_ERROR_CONTENT_NOT_ACCEPTED,
        PduHeaders.RESPONSE_STATUS_ERROR_PERMANENT_CONTENT_NOT_ACCEPTED
            -> ConversationListItem.Error.OUTGOING_CONTENT
        PduHeaders.RESPONSE_STATUS_ERROR_UNSUPPORTED_MESSAGE
            -> ConversationListItem.Error.OUTGOING_UNSUPPORTED
        MessageData.RAW_TELEPHONY_STATUS_MESSAGE_TOO_BIG
            -> ConversationListItem.Error.OUTGOING_TOO_LARGE
        else
            -> ConversationListItem.Error.UNKNOWN
    }

private fun isDraft(showDraft: Boolean, messageStatus: Int): Boolean =
    showDraft
            || messageStatus == MessageData.BUGLE_STATUS_OUTGOING_DRAFT
            // also check for unknown status which we get because sometimes the conversation
            // row is left with a latest_message_id of a no longer existing message and
            // therefore the join values come back as null (or in this case zero).
            || messageStatus == MessageData.BUGLE_STATUS_UNKNOWN

private fun isSending(messageStatus: Int): Boolean =
    messageStatus == MessageData.BUGLE_STATUS_OUTGOING_YET_TO_SEND ||
            messageStatus == MessageData.BUGLE_STATUS_OUTGOING_AWAITING_RETRY ||
            messageStatus == MessageData.BUGLE_STATUS_OUTGOING_SENDING ||
            messageStatus == MessageData.BUGLE_STATUS_OUTGOING_RESENDING

private fun getConversationListIcon(
    uri: String?
): ConversationListAvatar {
    val uri = uri?.toUri()
    if (uri != null) {
        if (uri.path == "/r") {
            return ConversationListAvatar.Media(uri.getQueryParameter("m").orEmpty())
        } else if (uri.path == "/l") {
            return ConversationListAvatar.Initials(
                uri.getQueryParameter("n")?.first().toString(),
                name = uri.getQueryParameter("n").orEmpty()
            )
        } else if (uri.path == "/g") {
            return ConversationListAvatar.Group(
                icons = uri.getQueryParameters("p").map {
                    getConversationListIcon(it)
                }
            )
        }
    }
    return ConversationListAvatar.Default(name = uri?.getQueryParameter("i").orEmpty())
}

private const val COLUMN_INDEX_ID = 0
private const val COLUMN_INDEX_NAME = 1
private const val COLUMN_INDEX_ICON = 2
private const val COLUMN_INDEX_SNIPPET_TEXT = 3
private const val COLUMN_INDEX_SORT_TIMESTAMP = 4
private const val COLUMN_INDEX_READ = 5
private const val COLUMN_INDEX_PREVIEW_URI = 6
private const val COLUMN_INDEX_PREVIEW_CONTENT_TYPE = 7
private const val COLUMN_INDEX_PARTICIPANT_CONTACT_ID = 8
private const val COLUMN_INDEX_PARTICIPANT_LOOKUP_KEY = 9
private const val COLUMN_INDEX_PARTICIPANT_NORMALIZED_DESTINATION = 10
private const val COLUMN_INDEX_PARTICIPANT_COUNT = 11
private const val COLUMN_INDEX_CURRENT_SELF_ID = 12
private const val COLUMN_INDEX_NOTIFICATION_ENABLED = 13
private const val COLUMN_INDEX_NOTIFICATION_SOUND_URI = 14
private const val COLUMN_INDEX_NOTIFICATION_VIBRATION = 15
private const val COLUMN_INDEX_INCLUDE_EMAIL_ADDR = 16
private const val COLUMN_INDEX_MESSAGE_STATUS = 17
private const val COLUMN_INDEX_SHOW_DRAFT = 18
private const val COLUMN_INDEX_DRAFT_PREVIEW_URI = 19
private const val COLUMN_INDEX_DRAFT_PREVIEW_CONTENT_TYPE = 20
private const val COLUMN_INDEX_DRAFT_SNIPPET_TEXT = 21
private const val COLUMN_INDEX_ARCHIVE_STATUS = 22
private const val COLUMN_INDEX_MESSAGE_ID = 23
private const val COLUMN_INDEX_SUBJECT_TEXT = 24
private const val COLUMN_INDEX_DRAFT_SUBJECT_TEXT = 25
private const val COLUMN_INDEX_RAW_STATUS = 26
private const val COLUMN_INDEX_SNIPPET_SENDER_FIRST_NAME = 27
private const val COLUMN_INDEX_SNIPPET_SENDER_DISPLAY_DESTINATION = 28
private const val COLUMN_INDEX_IS_ENTERPRISE = 29

package com.android.messaging.conversationlist.data.repository

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.net.toUri
import com.android.messaging.BuildConfig
import com.android.messaging.conversationlist.data.toConversationListItem
import com.android.messaging.conversationlist.domain.model.ConversationListItem
import com.android.messaging.conversationlist.domain.repository.ConversationListRepository
import com.android.messaging.datamodel.BugleDatabaseOperations
import com.android.messaging.datamodel.DataModel
import com.android.messaging.datamodel.action.DeleteConversationAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val AUTHORITY = "${BuildConfig.APPLICATION_ID}.datamodel.MessagingContentProvider"
private const val CONTENT_AUTHORITY = "content://${AUTHORITY}/"
private const val CONVERSATIONS_QUERY = "conversations"

val CONVERSATIONS_URI = (CONTENT_AUTHORITY + CONVERSATIONS_QUERY).toUri()

class ConversationListRepositoryImpl(
    val contentResolver: ContentResolver
) : ConversationListRepository {
    override fun conversationsFlow(
        isArchived: Boolean
    ): Flow<List<ConversationListItem>> {
        return callbackFlow {
            suspend fun emitNow() {
                val items = withContext(Dispatchers.IO) { getConversations(isArchived) }
                trySend(items).isSuccess
            }

            val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean) {
                    launch { emitNow() }
                }

                override fun onChange(selfChange: Boolean, uri: Uri?) {
                    launch { emitNow() }
                }
            }

            contentResolver.registerContentObserver(
                CONVERSATIONS_URI,
                true,
                observer
            )

            emitNow()

            awaitClose {
                contentResolver.unregisterContentObserver(observer)
            }
        }.distinctUntilChanged()
            .conflate()
            .flowOn(Dispatchers.IO)
    }

    override suspend fun archiveConversation(
        conversationId: String
    ) {
        val db = DataModel.get().getDatabase()
        db.beginTransaction()
        try {
            BugleDatabaseOperations.updateConversationArchiveStatusInTransaction(
                db,
                conversationId,
                true
            )
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        contentResolver.notifyChange(CONVERSATIONS_URI, null)
    }

    override suspend fun unarchiveConversations(conversationsId: List<String>) {
        val db = DataModel.get().getDatabase()
        db.beginTransaction()
        try {
            conversationsId.forEach { conversationId ->
                BugleDatabaseOperations.updateConversationArchiveStatusInTransaction(
                    db,
                    conversationId,
                    false
                )
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        contentResolver.notifyChange(CONVERSATIONS_URI, null)
    }

    override suspend fun archiveConversations(conversationsId: List<String>) {
        val db = DataModel.get().getDatabase()
        db.beginTransaction()
        try {
            conversationsId.forEach { conversationId ->
                BugleDatabaseOperations.updateConversationArchiveStatusInTransaction(
                    db,
                    conversationId,
                    true
                )
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        contentResolver.notifyChange(CONVERSATIONS_URI, null)
    }

    override suspend fun deleteConversations(conversations: List<Pair<String, Long>>) {
        //TODO: Implement here
        val action = DeleteConversationAction(conversations.map {
            DeleteConversationAction.TargetConversation(
                it.first,
                it.second
            )
        }.toTypedArray())
        action.start()
    }

    private fun getConversations(
        isArchived: Boolean
    ): List<ConversationListItem> {
        val cursor = contentResolver.query(
            CONVERSATIONS_URI,
            getConversationProjection(),
            getQueryArgs(isArchived),
            null
        )

        return buildList {
            cursor?.use {
                while (it.moveToNext()) {
                    add(cursor.toConversationListItem(false))
                }
            }
        }
    }

    private fun getConversationProjection(): Array<String> = arrayOf(
        COLUMN_NAME_ID,
        COLUMN_NAME_NAME,
        COLUMN_NAME_ICON,
        COLUMN_NAME_SNIPPET_TEXT,
        COLUMN_NAME_SORT_TIMESTAMP,
        COLUMN_NAME_READ,
        COLUMN_NAME_PREVIEW_URI,
        COLUMN_NAME_PREVIEW_CONTENT_TYPE,
        COLUMN_NAME_PARTICIPANT_CONTACT_ID,
        COLUMN_NAME_PARTICIPANT_LOOKUP_KEY,
        COLUMN_NAME_PARTICIPANT_NORMALIZED_DESTINATION,
        COLUMN_NAME_PARTICIPANT_COUNT,
        COLUMN_NAME_CURRENT_SELF_ID,
        COLUMN_NAME_NOTIFICATION_ENABLED,
        COLUMN_NAME_NOTIFICATION_SOUND_URI,
        COLUMN_NAME_NOTIFICATION_VIBRATION,
        COLUMN_NAME_INCLUDE_EMAIL_ADDR,
        COLUMN_NAME_MESSAGE_STATUS,
        COLUMN_NAME_SHOW_DRAFT,
        COLUMN_NAME_DRAFT_PREVIEW_URI,
        COLUMN_NAME_DRAFT_PREVIEW_CONTENT_TYPE,
        COLUMN_NAME_DRAFT_SNIPPET_TEXT,
        COLUMN_NAME_ARCHIVE_STATUS,
        COLUMN_NAME_MESSAGE_ID,
        COLUMN_NAME_SUBJECT_TEXT,
        COLUMN_NAME_DRAFT_SUBJECT_TEXT,
        COLUMN_NAME_RAW_STATUS,
        COLUMN_NAME_SNIPPET_SENDER_FIRST_NAME,
        COLUMN_NAME_SNIPPET_SENDER_DISPLAY_DESTINATION,
        COLUMN_NAME_IS_ENTERPRISE
    )

    private fun getQueryArgs(isArchived: Boolean): Bundle {
        return Bundle().apply {
            putString(
                ContentResolver.QUERY_ARG_SQL_SELECTION,
                "$COLUMN_NAME_ARCHIVE_STATUS = ?"
            )

            putStringArray(
                ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS,
                arrayOf(
                    (if (isArchived) 1 else 0).toString()
                )
            )

            putString(
                ContentResolver.QUERY_ARG_SQL_SORT_ORDER,
                "$COLUMN_NAME_SORT_TIMESTAMP DESC"
            )
        }
    }
}

private const val COLUMN_NAME_ID = "_id"
private const val COLUMN_NAME_NAME = "name"
private const val COLUMN_NAME_CURRENT_SELF_ID = "current_self_id"
private const val COLUMN_NAME_ARCHIVE_STATUS = "archive_status"
private const val COLUMN_NAME_READ = "read"
private const val COLUMN_NAME_ICON = "icon"
private const val COLUMN_NAME_PARTICIPANT_CONTACT_ID = "participant_contact_id"
private const val COLUMN_NAME_PARTICIPANT_LOOKUP_KEY = "participant_lookup_key"
private const val COLUMN_NAME_PARTICIPANT_NORMALIZED_DESTINATION =
    "participant_normalized_destination"
private const val COLUMN_NAME_SORT_TIMESTAMP = "sort_timestamp"
private const val COLUMN_NAME_SHOW_DRAFT = "show_draft"
private const val COLUMN_NAME_DRAFT_SNIPPET_TEXT = "draft_snippet_text"
private const val COLUMN_NAME_DRAFT_PREVIEW_URI = "draft_preview_uri"
private const val COLUMN_NAME_DRAFT_SUBJECT_TEXT = "draft_subject_text"
private const val COLUMN_NAME_DRAFT_PREVIEW_CONTENT_TYPE = "draft_preview_content_type"
private const val COLUMN_NAME_PREVIEW_URI = "preview_uri"
private const val COLUMN_NAME_PREVIEW_CONTENT_TYPE = "preview_content_type"
private const val COLUMN_NAME_PARTICIPANT_COUNT = "participant_count"
private const val COLUMN_NAME_NOTIFICATION_ENABLED = "notification_enabled"
private const val COLUMN_NAME_NOTIFICATION_SOUND_URI = "notification_sound_uri"
private const val COLUMN_NAME_NOTIFICATION_VIBRATION = "notification_vibration"
private const val COLUMN_NAME_INCLUDE_EMAIL_ADDR = "include_email_addr"
private const val COLUMN_NAME_MESSAGE_STATUS = "message_status"
private const val COLUMN_NAME_RAW_STATUS = "raw_status"
private const val COLUMN_NAME_MESSAGE_ID = "message_id"
private const val COLUMN_NAME_SNIPPET_SENDER_FIRST_NAME = "snippet_sender_first_name"
private const val COLUMN_NAME_SNIPPET_SENDER_DISPLAY_DESTINATION =
    "snippet_sender_display_destination"
private const val COLUMN_NAME_IS_ENTERPRISE = "IS_ENTERPRISE"
private const val COLUMN_NAME_SNIPPET_TEXT = "SNIPPET_TEXT"
private const val COLUMN_NAME_SUBJECT_TEXT = "SUBJECT_TEXT"

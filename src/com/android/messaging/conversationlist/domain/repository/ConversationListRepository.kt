package com.android.messaging.conversationlist.domain.repository

import com.android.messaging.conversationlist.domain.model.ConversationListItem
import kotlinx.coroutines.flow.Flow

interface ConversationListRepository {
    fun conversationsFlow(
        isArchived: Boolean
    ): Flow<List<ConversationListItem>>

    suspend fun archiveConversation(
        conversationId: String
    )

    suspend fun unarchiveConversations(
        conversationsId: List<String>
    )

    suspend fun archiveConversations(
        conversationsId: List<String>
    )

    suspend fun deleteConversations(conversations: List<Pair<String, Long>>)
}

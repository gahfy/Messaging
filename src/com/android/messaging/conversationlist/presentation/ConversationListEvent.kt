package com.android.messaging.conversationlist.presentation

sealed interface ConversationListEvent {
    data class RedirectToConversation(val conversationId: String) : ConversationListEvent
    object RedirectToArchived : ConversationListEvent
    object RedirectToSettings : ConversationListEvent
    object RedirectToNewConversation : ConversationListEvent
    data class ConversationArchived(val conversationIds: List<String>) : ConversationListEvent
    data class ConversationUnarchived(val count: Int) : ConversationListEvent
}

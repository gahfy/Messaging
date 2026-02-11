package com.android.messaging.conversationlist.domain.model

sealed interface ConversationListAvatar {
    data class Default(val name: String): ConversationListAvatar
    data class Initials(val initials: String, val name: String): ConversationListAvatar
    data class Media(val mediaUri: String): ConversationListAvatar
    data class Group(val icons: List<ConversationListAvatar>): ConversationListAvatar
}

data class ConversationListItem(
    val conversationId: String,
    val name: String,
    val avatar: ConversationListAvatar,
    val read: Boolean,
    val sortTimestamp: Long,
    val snippetText: String,
    val previewUri: String?,
    val previewContentType: String?,
    val participantContactId: Long,
    val participantLookupKey: String?,
    val participantNormalizedDestination: String?,
    val currentSelfId: String,
    val participantCount: Int,
    val notificationEnabled: Boolean,
    val notificationSoundUri: String?,
    val notificationVibrate: Boolean,
    val includeEmailAddress: Boolean,
    val messageStatus: Int,
    val rawStatus: Int,
    val error: Error?,
    val isDraft: Boolean,
    val isSending: Boolean,
    val showDraft: Boolean,
    val draftPreviewUri: String?,
    val draftPreviewContentType: String?,
    val draftSnippetText: String?,
    val archived: Boolean,
    val subject: String?,
    val draftSubject: String?,
    val snippetSenderFirstName: String?,
    val snippetSenderDisplayDestination: String,
    val isEnterprise: Boolean
) {
    enum class Error {
        UNKNOWN,
        OUTGOING_SERVICE,
        OUTGOING_ADDRESS,
        OUTGOING_CORRUPT,
        OUTGOING_CONTENT,
        OUTGOING_UNSUPPORTED,
        OUTGOING_TOO_LARGE,
        DOWNLOAD
    }
}

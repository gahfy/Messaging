package com.android.messaging.conversationlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.messaging.conversationlist.domain.repository.ConversationListRepository
import com.android.messaging.conversationlist.presentation.ConversationListState.Loaded.ConversationRow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConversationListViewModel(
    private val conversationListRepository: ConversationListRepository
) : ViewModel() {
    private val isArchived = MutableStateFlow(false)
    private val currentlySelectedIds: MutableStateFlow<Set<String>> = MutableStateFlow(hashSetOf())

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<ConversationListState> =
        isArchived
            .flatMapLatest { archived ->
                conversationListRepository.conversationsFlow(archived)
            }
            .map { items ->
                when {
                    items.isEmpty() -> ConversationListState.Empty(
                        type = ConversationListState.Empty.Type.EMPTY,
                        onArchivedClickedMember = this@ConversationListViewModel::onArchivedClicked,
                        onSettingsClickedMember = this@ConversationListViewModel::onSettingsClicked
                    )

                    else -> {
                        items.toConversationListStateLoaded(
                            onConversationClicked = this@ConversationListViewModel::onConversationClicked,
                            onConversationLongClicked = this@ConversationListViewModel::onConversationLongClicked,
                            onConversationSwiped = this@ConversationListViewModel::onConversationSwiped,
                            onConversationsUnarchived = this@ConversationListViewModel::onUnarchiveClicked,
                            onArchivedClicked = this@ConversationListViewModel::onArchivedClicked,
                            onSettingsClicked = this@ConversationListViewModel::onSettingsClicked,
                            onDeleteActionClicked = this@ConversationListViewModel::onDeleteActionClicked,
                            onArchiveActionClicked = this@ConversationListViewModel::onArchiveActionClicked,
                            onNewConversationClicked = this@ConversationListViewModel::onNewConversationClicked,
                            events = this@ConversationListViewModel.events,
                            swipeEnabled = true,
                            displayActions = false
                        )
                    }
                }
            }
            .combine(currentlySelectedIds) { state, selectedIds ->
                if (state is ConversationListState.Loaded) {
                    state.copy(
                        conversations = state.conversations.map {
                            it.copy(
                                selected = selectedIds.contains(it.id)
                            )
                        },
                        swipeEnabled = selectedIds.isEmpty(),
                        displayActionsMember = selectedIds.isNotEmpty()
                    )
                } else {
                    state
                }
            }
            .onStart {
                emit(
                    ConversationListState.Empty(
                        type = ConversationListState.Empty.Type.LOADING,
                        onArchivedClickedMember = this@ConversationListViewModel::onArchivedClicked,
                        onSettingsClickedMember = this@ConversationListViewModel::onSettingsClicked
                    )
                )
            }
            .catch { _ ->
                emit(
                    ConversationListState.Empty(
                        type = ConversationListState.Empty.Type.EMPTY,
                        onArchivedClickedMember = this@ConversationListViewModel::onArchivedClicked,
                        onSettingsClickedMember = this@ConversationListViewModel::onSettingsClicked
                    )
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ConversationListState.Empty(
                    type = ConversationListState.Empty.Type.LOADING,
                    onArchivedClickedMember = this@ConversationListViewModel::onArchivedClicked,
                    onSettingsClickedMember = this@ConversationListViewModel::onSettingsClicked
                )
            )

    private val _events = MutableSharedFlow<ConversationListEvent>(extraBufferCapacity = 64)
    val events = _events.asSharedFlow()

    fun onBackPressed(): Boolean {
        if (currentlySelectedIds.value.isEmpty()) {
            return false
        } else {
            viewModelScope.launch {
                currentlySelectedIds.update {
                    mutableSetOf()
                }
            }
            return true
        }
    }

    fun onConversationClicked(conversationRow: ConversationRow) {
        if (currentlySelectedIds.value.isEmpty()) {
            _events.tryEmit(ConversationListEvent.RedirectToConversation(conversationRow.id))
        } else {
            onConversationLongClicked(conversationRow)
        }
    }

    fun onConversationLongClicked(conversationRow: ConversationRow) {
        viewModelScope.launch {
            currentlySelectedIds.update {
                it.toMutableSet().apply {
                    if (!add(conversationRow.id)) remove(conversationRow.id)
                }.toSet()
            }
        }
    }

    fun onArchivedClicked() {
        _events.tryEmit(ConversationListEvent.RedirectToArchived)
    }

    fun onSettingsClicked() {
        _events.tryEmit(ConversationListEvent.RedirectToSettings)
    }

    fun onNewConversationClicked() {
        _events.tryEmit(ConversationListEvent.RedirectToNewConversation)
    }

    fun onConversationSwiped(conversationRow: ConversationRow) {
        viewModelScope.launch(Dispatchers.IO) {
            conversationListRepository.archiveConversation(conversationRow.id)
            _events.tryEmit(ConversationListEvent.ConversationArchived(listOf(conversationRow.id)))
        }
    }

    fun onUnarchiveClicked(conversationIds: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            conversationListRepository.unarchiveConversations(conversationIds)
            _events.tryEmit(ConversationListEvent.ConversationUnarchived(conversationIds.size))
        }
    }

    fun onArchiveActionClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            (state.value as? ConversationListState.Loaded)
                ?.conversations
                ?.filter { currentlySelectedIds.value.contains(it.id) }
                ?.map {
                    it.id
                }?.let {
                    conversationListRepository.archiveConversations(it)
                }
            _events.tryEmit(ConversationListEvent.ConversationArchived(currentlySelectedIds.value.toList()))
            currentlySelectedIds.update {
                mutableSetOf()
            }
        }
    }

    fun onDeleteActionClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            (state.value as? ConversationListState.Loaded)
                ?.conversations
                ?.filter { currentlySelectedIds.value.contains(it.id) }
                ?.map {
                    Pair(it.id, it.timestamp)
                }?.let {
                    conversationListRepository.deleteConversations(it)
                }
            currentlySelectedIds.update {
                mutableSetOf()
            }
        }
    }
}

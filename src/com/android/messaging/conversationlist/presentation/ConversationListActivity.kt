package com.android.messaging.conversationlist.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.android.messaging.conversationlist.data.repository.ConversationListRepositoryImpl
import com.android.messaging.presentation.theme.AppTheme
import com.android.messaging.ui.UIIntents
import com.android.messaging.util.UiUtils
import kotlinx.coroutines.launch

class ConversationListActivity : ComponentActivity() {
    private val viewModel: ConversationListViewModel by viewModels {
        val repository = ConversationListRepositoryImpl(contentResolver)

        viewModelFactory {
            initializer {
                ConversationListViewModel(repository)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (UiUtils.redirectToPermissionCheckIfNeeded(this)) {
            return
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is ConversationListEvent.RedirectToConversation ->
                            redirectToConversation(event.conversationId)

                        ConversationListEvent.RedirectToSettings ->
                            redirectToSettings()

                        ConversationListEvent.RedirectToArchived ->
                            redirectToArchived()

                        ConversationListEvent.RedirectToNewConversation ->
                            redirectToNewConversation()

                        else -> {}
                    }
                }
            }
        }

        val callback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                if (!viewModel.onBackPressed()) {
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        onBackPressedDispatcher.addCallback(
            this, // LifecycleOwner
            callback
        )

        setContent {
            val state by viewModel.state.collectAsState()
            AppTheme {
                ConversationListScreen(state)
            }
        }
    }

    private fun redirectToConversation(
        conversationId: String
    ) {
        val sceneTransitionAnimationOptions: Bundle? = null
        val hasCustomTransitions = false

        UIIntents.get().launchConversationActivity(
            this, conversationId, null,
            sceneTransitionAnimationOptions,
            hasCustomTransitions
        )
    }

    private fun redirectToArchived() {
        UIIntents.get().launchArchivedConversationsActivity(this)
    }

    private fun redirectToNewConversation() {
        UIIntents.get().launchCreateNewConversationActivity(this, null)
    }

    private fun redirectToSettings() {
        UIIntents.get().launchSettingsActivity(this)
    }
}

package com.android.messaging.ui.conversationlist

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.accessibility.AccessibilityManager
import android.widget.AbsListView
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.core.view.ViewGroupCompat
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.messaging.R
import com.android.messaging.annotation.VisibleForAnimation
import com.android.messaging.datamodel.DataModel
import com.android.messaging.datamodel.binding.Binding
import com.android.messaging.datamodel.binding.BindingBase
import com.android.messaging.datamodel.data.ConversationListData
import com.android.messaging.datamodel.data.ConversationListData.ConversationListDataListener
import com.android.messaging.datamodel.data.ConversationListItemData
import com.android.messaging.ui.BugleAnimationTags
import com.android.messaging.ui.ListEmptyView
import com.android.messaging.ui.SnackBarInteraction
import com.android.messaging.ui.UIIntents
import com.android.messaging.util.AccessibilityUtil
import com.android.messaging.util.Assert
import com.android.messaging.util.ImeUtil
import com.android.messaging.util.LogUtil
import com.android.messaging.util.UiUtils

private const val BUNDLE_ARCHIVED_MODE = "archived_mode"
private const val BUNDLE_FORWARD_MESSAGE_MODE = "forward_message_mode"
private const val VERBOSE = false
private const val SAVED_INSTANCE_STATE_LIST_VIEW_STATE_KEY = "conversationListViewState"
/**
 * Shows a list of conversations.
 */
class ConversationListFragment: Fragment(), ConversationListDataListener, ConversationListItemView.HostInterface {
    private var showBlockedMenuItem: MenuItem? = null
    private var archiveMode: Boolean = false
    private var blockedAvailable: Boolean = false
    private var forwardMessageMode: Boolean = false
    private var host : ConversationListFragmentHost? = null
    private var recyclerView: RecyclerView? = null

    private var startNewConversationButton: ImageView? = null
    private var emptyListMessageView: ListEmptyView? = null

    private val adapter: ConversationListAdapter by lazy {
        ConversationListAdapter(activity, null, this)
    }
    private var listState: Parcelable? = null
    private val listBinding: Binding<ConversationListData> = BindingBase.createBinding(this)

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        listBinding.getData().init(LoaderManager.getInstance(this), listBinding)
    }

    override fun onResume() {
        super.onResume()

        Assert.notNull(host)
        setScrolledToNewestConversationIfNeeded()

        updateUi()
    }

    fun setScrolledToNewestConversationIfNeeded() {
        if (!archiveMode
            && !forwardMessageMode
            && isScrolledToFirstConversation()
            && host?.hasWindowFocus() == true) {
            listBinding.getData().setScrolledToNewestConversation(true)
        }
    }

    private fun isScrolledToFirstConversation(): Boolean {
        val firstItemPosition = (recyclerView?.layoutManager as? LinearLayoutManager)?.findFirstCompletelyVisibleItemPosition()
        return firstItemPosition == null || firstItemPosition == 0
    }

    override fun onDestroy() {
        super.onDestroy()
        listBinding.unbind()
        host = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.conversation_list_fragment, container, false) as ViewGroup
        recyclerView = rootView.findViewById(android.R.id.list)
        emptyListMessageView = rootView.findViewById(R.id.no_conversations_view)
        emptyListMessageView?.setImageHint(R.drawable.ic_oobe_conv_list)
        // The default behavior for default layout param generation by LinearLayoutManager is to
        // provide width and height of WRAP_CONTENT, but this is not desirable for
        // ConversationListFragment; the view in each row should be a width of MATCH_PARENT so that
        // the entire row is tappable.
        val manager : LinearLayoutManager= object: LinearLayoutManager(requireActivity()) {
            override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
                return RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        }

        recyclerView?.layoutManager = manager
        recyclerView?.setHasFixedSize(true)
        recyclerView?.adapter = adapter
        recyclerView?.setOnScrollListener(object: RecyclerView.OnScrollListener() {
            var currentState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (currentState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
                    || currentState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    ImeUtil.get().hideImeKeyboard(requireActivity(), recyclerView)
                }

                if (isScrolledToFirstConversation()) {
                    setScrolledToNewestConversationIfNeeded()
                } else {
                    listBinding.getData().setScrolledToNewestConversation(false)
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                currentState = newState
            }
        })
        recyclerView?.addOnItemTouchListener(ConversationListSwipeHelper(recyclerView))
        savedInstanceState?.let {
            listState = it.getParcelable(SAVED_INSTANCE_STATE_LIST_VIEW_STATE_KEY)
        }

        startNewConversationButton = rootView.findViewById(R.id.start_new_conversation_button)
        if (archiveMode) {
            startNewConversationButton?.setVisibility(View.GONE)
        } else {
            startNewConversationButton?.setVisibility(View.VISIBLE)
            startNewConversationButton?.setOnClickListener(object: View.OnClickListener {
                override fun onClick(clickView: View) {
                    host?.onCreateConversationClick()
                }
            })
        }
        startNewConversationButton?.let {
            ViewCompat.setTransitionName(it, BugleAnimationTags.TAG_FABICON)
        }

        // The root view has a non-null background, which by default is deemed by the framework
        // to be a "transition group," where all child views are animated together during an
        // activity transition. However, we want each individual items in the recycler view to
        // show explode animation themselves, so we explicitly tag the root view to be a non-group.
        ViewGroupCompat.setTransitionGroup(rootView, false)

        setHasOptionsMenu(true)
        return rootView
    }

    @Deprecated("Deprecated in parent")
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        if (VERBOSE) {
            LogUtil.v(LogUtil.BUGLE_TAG, "Attaching List")
        }
        val arguments = getArguments()
        if (arguments != null) {
            archiveMode = arguments.getBoolean(BUNDLE_ARCHIVED_MODE, false)
            forwardMessageMode = arguments.getBoolean(BUNDLE_FORWARD_MESSAGE_MODE, false)
        }
        listBinding.bind(DataModel.get().createConversationListData(activity, this, archiveMode))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (listState != null) {
            outState.putParcelable(SAVED_INSTANCE_STATE_LIST_VIEW_STATE_KEY, listState)
        }
    }

    override fun onPause() {
        super.onPause()
        listState = recyclerView?.layoutManager?.onSaveInstanceState()
        listBinding.getData().setScrolledToNewestConversation(false)
    }

    fun setHost(host: ConversationListFragmentHost) {
        Assert.isNull(this.host)
        this.host = host
    }

    override fun onConversationListCursorUpdated(data: ConversationListData?, cursor: Cursor?) {
        listBinding.ensureBound(data)
        val oldCursor: Cursor? = adapter.swapCursor(cursor)
        updateEmptyListUi((cursor?.count?:0) == 0)
        if (listState != null && cursor != null && oldCursor == null) {
            recyclerView?.layoutManager?.onRestoreInstanceState(listState)
        }
    }

    override fun setBlockedParticipantsAvailable(blockedAvailable: Boolean) {
        this.blockedAvailable = blockedAvailable
        showBlockedMenuItem?.isVisible = blockedAvailable
    }

    fun updateUi() {
        adapter.notifyDataSetChanged()
    }

    @Deprecated("Deprecated in parent")
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_start_new_conversation)?.let {
            // It is recommended for the Floating Action button functionality to be duplicated as a
            // menu
            val accessibilityManager: AccessibilityManager =
            activity?.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
            it.isVisible = accessibilityManager.isTouchExplorationEnabled
        }

        menu.findItem(R.id.action_show_archived)?.isVisible = true
    }

    @Deprecated("Deprecated in parent")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (!isAdded()) {
            // Guard against being called before we're added to the activity
            return
        }

        showBlockedMenuItem = menu.findItem(R.id.action_show_blocked_contacts)
        showBlockedMenuItem?.setVisible(blockedAvailable)
    }

    override fun onConversationClicked(conversationListItemData: ConversationListItemData, isLongClick: Boolean, conversationView: ConversationListItemView) {
        val listData = listBinding.getData()
        host?.onConversationClick(listData, conversationListItemData, isLongClick,
            conversationView)
    }

    override fun isConversationSelected(conversationId: String): Boolean {
        return host?.isConversationSelected(conversationId) == true
    }

    override fun isSwipeAnimatable(): Boolean {
        return host?.isSwipeAnimatable() == true
    }

    private fun updateEmptyListUi(isEmpty: Boolean) {
        if (isEmpty) {
            val emptyListText =
            if (!listBinding.getData().getHasFirstSyncCompleted()) {
                R.string.conversation_list_first_sync_text
            } else if (archiveMode) {
                R.string.archived_conversation_list_empty_text
            } else {
                R.string.conversation_list_empty_text
            }
            emptyListMessageView?.setTextHint(emptyListText)
            emptyListMessageView?.visibility = View.VISIBLE
            emptyListMessageView?.setIsImageVisible(true)
            emptyListMessageView?.setIsVerticallyCentered(true)
        } else {
            emptyListMessageView?.visibility = View.GONE
        }
    }

    override fun getSnackBarInteractions(): List<SnackBarInteraction> {
        val fabInteraction = SnackBarInteraction.BasicSnackBarInteraction(startNewConversationButton)
        val interactions = listOf(fabInteraction)
        return interactions
    }

    private fun getNormalizedFabAnimator(): ViewPropertyAnimator? {
        return activity?.resources?.getInteger(R.integer.fab_animation_duration_ms)?.toLong()?.let {
            startNewConversationButton?.animate()
                ?.setInterpolator(UiUtils.DEFAULT_INTERPOLATOR)
                ?.setDuration(it)
        }
    }

    fun dismissFab(): ViewPropertyAnimator? {
        // To prevent clicking while animating.
        startNewConversationButton?.setEnabled(false)
        val lp = startNewConversationButton?.layoutParams as? ViewGroup.MarginLayoutParams
        val fabWidthWithLeftRightMargin: Float = (startNewConversationButton?.getWidth()?.toFloat()?:0f) +
            (lp?.leftMargin?.toFloat()?:0f) +
            (lp?.rightMargin?.toFloat()?:0f)
        val direction = if(AccessibilityUtil.isLayoutRtl(startNewConversationButton)) -1 else 1
        return getNormalizedFabAnimator()?.translationX(direction * fabWidthWithLeftRightMargin)
    }

    fun showFab(): ViewPropertyAnimator? {
        return getNormalizedFabAnimator()?.translationX(0f)?.withEndAction { // Re-enable clicks after the animation.
            startNewConversationButton?.setEnabled(true)
        }
    }

    fun getHeroElementForTransition(): View? {
        return if(archiveMode) null else startNewConversationButton
    }

    @VisibleForAnimation
    fun getRecyclerView(): RecyclerView? {
        return recyclerView
    }

    override fun startFullScreenPhotoViewer(initialPhoto: Uri, initialPhotoBounds: Rect, photosUri: Uri) {
        UIIntents.get().launchFullScreenPhotoViewer(activity, initialPhoto, initialPhotoBounds, photosUri)
    }

    override fun startFullScreenVideoViewer(videoUri: Uri) {
        UIIntents.get().launchFullScreenVideoViewer(activity, videoUri)
    }

    override fun isSelectionMode(): Boolean {
        return host?.isSelectionMode() == true
    }

    companion object {
        fun createArchivedConversationListFragment(): ConversationListFragment =
            createConversationListFragment(BUNDLE_ARCHIVED_MODE)

        fun createForwardMessageConversationListFragment(): ConversationListFragment =
            createConversationListFragment(BUNDLE_FORWARD_MESSAGE_MODE)

        fun createConversationListFragment(modeKeyName: String): ConversationListFragment {
            val fragment: ConversationListFragment = ConversationListFragment()
            val bundle: Bundle = Bundle()
            bundle.putBoolean(modeKeyName, true)
            fragment.setArguments(bundle)
            return fragment
        }
    }

    interface ConversationListFragmentHost {
        fun onConversationClick(
            listData: ConversationListData,
            conversationListItemData: ConversationListItemData,
            isLongClick: Boolean,
            conversationView: ConversationListItemView
        )
        fun onCreateConversationClick()
        fun isConversationSelected(conversationId: String): Boolean
        fun isSwipeAnimatable(): Boolean
        fun  isSelectionMode(): Boolean
        fun  hasWindowFocus(): Boolean
    }
}
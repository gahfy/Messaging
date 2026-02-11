package com.android.messaging.presentation.utils

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow

@Composable
fun rememberIsScrollingDown(listState: LazyListState): State<Boolean> {
    val isScrollingDown = remember { mutableStateOf(false) }

    LaunchedEffect(listState) {
        var lastIndex = listState.firstVisibleItemIndex
        var lastOffset = listState.firstVisibleItemScrollOffset

        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                isScrollingDown.value =
                    (index > lastIndex) || (index == lastIndex && offset > lastOffset)

                lastIndex = index
                lastOffset = offset
            }
    }

    return isScrollingDown
}

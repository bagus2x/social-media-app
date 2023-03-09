package bagus2x.sosmed.presentation.common

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

@Composable
fun <T : Any> LazyPagingItems<T>.rememberLazyListState(): LazyListState {
    // After recreation, LazyPagingItems first return 0 items, then the cached items.
    // This behavior/issue is resetting the LazyListState scroll position.
    // Below is a workaround. More info: https://issuetracker.google.com/issues/177245496.
    return when (itemCount) {
        // Return a different LazyListState instance.
        0 -> remember(this) { LazyListState(0, 0) }
        // Return rememberLazyListState (normal case).
        else -> androidx.compose.foundation.lazy.rememberLazyListState()
    }
}

@Composable
fun <T : Any> LazyPagingItems<T>.isEmpty(): Boolean {
    val isVisible by remember { derivedStateOf { itemSnapshotList.size == 0 } }
    return isVisible
}

@Composable
fun <T : Any> LazyPagingItems<T>.isEmptyAndNotLoading(): Boolean {
    val isVisible by remember {
        derivedStateOf {
            itemSnapshotList.size == 0 && loadState.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached
        }
    }
    return isVisible
}

@Composable
fun <T : Any> LazyPagingItems<T>.isLoading(): Boolean {
    val isVisible by remember {
        derivedStateOf { itemSnapshotList.size == 0 && loadState.refresh == LoadState.Loading }
    }
    return isVisible
}

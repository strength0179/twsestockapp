package com.example.twsestockapp.ui.main.component

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Synchronizes the vertical list position of every stock-pager page.
 *
 * Every Pager page owns an independent [LazyListState].
 *
 * Only the currently settled page is allowed to act as the source while
 * its list is actively scrolling. All other page lists are immediately
 * moved to the same:
 *
 * - first visible item index
 * - first visible item scroll offset
 *
 * This keeps the same stock row visible when the user changes fields.
 */
@Composable
internal fun StockPagerScrollSync(
    pagerState: PagerState,
    listStates: List<LazyListState>,
) {
    listStates.forEachIndexed { pageIndex, sourceState ->
        LaunchedEffect(
            pageIndex,
            sourceState,
            pagerState,
            listStates,
        ) {
            snapshotFlow {
                StockPagerListPosition(
                    firstVisibleItemIndex =
                        sourceState.firstVisibleItemIndex,
                    firstVisibleItemScrollOffset =
                        sourceState.firstVisibleItemScrollOffset,
                    isScrollInProgress =
                        sourceState.isScrollInProgress,
                    settledPage =
                        pagerState.settledPage,
                )
            }
                .distinctUntilChanged()
                .collectLatest { position ->
                    val isActiveSource =
                        position.isScrollInProgress &&
                            position.settledPage ==
                            pageIndex

                    if (!isActiveSource) {
                        return@collectLatest
                    }

                    synchronizeTargetLists(
                        sourcePageIndex =
                            pageIndex,
                        position =
                            position,
                        listStates =
                            listStates,
                    )
                }
        }
    }
}

/**
 * Immediately moves every inactive Pager list to the position of the
 * active source list.
 */
private suspend fun synchronizeTargetLists(
    sourcePageIndex: Int,
    position: StockPagerListPosition,
    listStates: List<LazyListState>,
) {
    listStates.forEachIndexed {
            targetPageIndex,
            targetState ->

        if (
            targetPageIndex !=
            sourcePageIndex
        ) {
            targetState.scrollToItem(
                index =
                    position.firstVisibleItemIndex,
                scrollOffset =
                    position.firstVisibleItemScrollOffset,
            )
        }
    }
}

/**
 * Snapshot of the information required to synchronize Pager lists.
 */
private data class StockPagerListPosition(
    val firstVisibleItemIndex: Int,
    val firstVisibleItemScrollOffset: Int,
    val isScrollInProgress: Boolean,
    val settledPage: Int,
)

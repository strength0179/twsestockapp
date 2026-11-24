package com.example.twsestockapp.ui.main.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.twsestockapp.data.model.Stock
import kotlinx.coroutines.launch

/**
 * Main-screen Pager for an already-resolved ordered collection of stock
 * fields.
 *
 * Responsibilities:
 *
 * - own PagerState
 * - create per-page LazyListState objects
 * - assemble Pager presentation
 * - connect gesture navigation to PagerState
 *
 * Delegated responsibilities:
 *
 * - field resolution: StockPagerPageSelection.kt
 * - gestures: StockPagerGesture.kt
 * - vertical synchronization: StockPagerScrollSync.kt
 * - title: StockPagerTitle.kt
 * - position indicator: StockPagerIndicator.kt
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun StockPager(
    stocks: List<Stock>,
    pages: List<StockPagerPage>,
    modifier: Modifier = Modifier,
) {
    /*
     * Pager cannot render safely without a selected field.
     *
     * The field editor normally prevents this state, but this guard
     * protects against empty or invalid persisted configuration.
     */
    if (pages.isEmpty()) {
        EmptyStockPager(
            modifier =
                modifier,
        )

        return
    }

    val pagerState =
        rememberPagerState(
            pageCount = {
                pages.size
            },
        )

    val coroutineScope =
        rememberCoroutineScope()

    /*
     * Every Pager page owns an independent LazyListState.
     *
     * StockPagerScrollSync keeps these states aligned vertically.
     */
    val listStates =
        remember(pages.size) {
            List(pages.size) {
                LazyListState()
            }
        }

    StockPagerScrollSync(
        pagerState =
            pagerState,
        listStates =
            listStates,
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .stockPagerGesture(
                pageCount =
                    pages.size,
                onSwipePrevious = {
                    val targetPage =
                        previousStockPagerPage(
                            currentPage =
                                pagerState.currentPage,
                            pageCount =
                                pages.size,
                        )

                    if (
                        targetPage !=
                        pagerState.currentPage
                    ) {
                        coroutineScope.launch {
                            /*
                             * Immediate page change without animation.
                             */
                            pagerState.scrollToPage(
                                targetPage
                            )
                        }
                    }
                },
                onSwipeNext = {
                    val targetPage =
                        nextStockPagerPage(
                            currentPage =
                                pagerState.currentPage,
                            pageCount =
                                pages.size,
                        )

                    if (
                        targetPage !=
                        pagerState.currentPage
                    ) {
                        coroutineScope.launch {
                            /*
                             * Immediate page change without animation.
                             */
                            pagerState.scrollToPage(
                                targetPage
                            )
                        }
                    }
                },
            ),
        horizontalAlignment =
            Alignment.CenterHorizontally,
    ) {
        StockPagerTitle(
            page =
                pages[pagerState.currentPage],
        )

        StockPagerIndicator(
            currentPage =
                pagerState.currentPage,
            pageCount =
                pages.size,
        )

        HorizontalPager(
            state =
                pagerState,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),

            /*
             * Native Pager dragging stays disabled.
             *
             * StockPagerGesture converts horizontal gestures into
             * immediate previous/next page requests.
             */
            userScrollEnabled =
                false,

            /*
             * Keep all selected pages composed so their independent
             * LazyListState objects can stay synchronized.
             */
            beyondViewportPageCount =
                (pages.size - 1)
                    .coerceAtLeast(0),

            key = { pageIndex ->
                pages[pageIndex].key
            },
        ) { pageIndex ->
            StockPagerList(
                stocks =
                    stocks,
                page =
                    pages[pageIndex],
                listState =
                    listStates[pageIndex],
                modifier =
                    Modifier.fillMaxSize(),
            )
        }
    }
}

/**
 * Defensive presentation used when no valid Pager field is available.
 */
@Composable
private fun EmptyStockPager(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier.fillMaxSize(),
        contentAlignment =
            Alignment.Center,
    ) {
        Text(
            text =
                "請選擇至少一個資料欄位",
        )
    }
}

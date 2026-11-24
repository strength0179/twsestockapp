package com.example.twsestockapp.ui.main.component

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.abs

/**
 * Adds horizontal swipe detection without directly moving UI content.
 *
 * The gesture is converted into one discrete navigation request:
 *
 * - swipe left  -> next page
 * - swipe right -> previous page
 *
 * Short horizontal movements below [swipeThreshold] are ignored.
 *
 * This modifier does not know about PagerState. It only reports the
 * intended direction to its caller.
 */
internal fun Modifier.stockPagerGesture(
    pageCount: Int,
    swipeThreshold: Float = 80f,
    onSwipePrevious: () -> Unit,
    onSwipeNext: () -> Unit,
): Modifier {
    return pointerInput(
        pageCount,
        swipeThreshold,
    ) {
        var horizontalDragDistance =
            0f

        detectHorizontalDragGestures(
            onDragStart = {
                horizontalDragDistance =
                    0f
            },
            onHorizontalDrag = {
                    change,
                    dragAmount ->

                horizontalDragDistance +=
                    dragAmount

                /*
                 * Consume the horizontal drag so content does not
                 * visually follow the user's finger.
                 */
                change.consume()
            },
            onDragCancel = {
                horizontalDragDistance =
                    0f
            },
            onDragEnd = {
                if (
                    pageCount > 1 &&
                    abs(horizontalDragDistance) >=
                    swipeThreshold
                ) {
                    if (
                        horizontalDragDistance < 0f
                    ) {
                        onSwipeNext()
                    } else {
                        onSwipePrevious()
                    }
                }

                horizontalDragDistance =
                    0f
            },
        )
    }
}

/**
 * Returns the next page using circular navigation.
 *
 * Example with four pages:
 *
 * 0 -> 1 -> 2 -> 3 -> 0
 */
internal fun nextStockPagerPage(
    currentPage: Int,
    pageCount: Int,
): Int {
    if (pageCount <= 1) {
        return 0
    }

    return (
        currentPage +
            1
    ) % pageCount
}

/**
 * Returns the previous page using circular navigation.
 *
 * Example with four pages:
 *
 * 0 -> 3 -> 2 -> 1 -> 0
 */
internal fun previousStockPagerPage(
    currentPage: Int,
    pageCount: Int,
): Int {
    if (pageCount <= 1) {
        return 0
    }

    return (
        currentPage -
            1 +
            pageCount
    ) % pageCount
}

package com.memeusix.ekspensify.ui.dashboard.home.bottomSheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.memeusix.ekspensify.components.BottomSheetDialog
import com.memeusix.ekspensify.components.VerticalSpace
import com.memeusix.ekspensify.ui.dashboard.budget.components.BottomSheetListItem
import com.memeusix.ekspensify.ui.dashboard.transactions.data.Displayable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Displayable> HomeInsightsBottomSheetDialog(
    options: List<T>,
    selectedRange: T,
    title: String,
    onClick: (T) -> Unit,
    onDismiss: () -> Unit,
) {
    val lazyListState = rememberLazyListState()
    BottomSheetDialog(
        title = title,
        onDismiss = onDismiss,
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            state = lazyListState,
        ) {
            items(
                options,
            ) { item ->
                BottomSheetListItem(
                    title = item.displayName,
                    isSelected = selectedRange == item,
                    leadingContent = {},
                    onClick = {
                        onClick(item)
                    }
                )
            }
            item { VerticalSpace(5.dp) }
        }
    }
}


package com.memeusix.budgetbuddy.ui.categories.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.memeusix.budgetbuddy.R
import com.memeusix.budgetbuddy.data.model.responseModel.CustomIconModel
import com.memeusix.budgetbuddy.ui.theme.Dark10
import com.memeusix.budgetbuddy.ui.theme.Light60
import com.memeusix.budgetbuddy.ui.theme.Violet80

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IconSelectionCard(
    icons: List<CustomIconModel>, selectedIcon: MutableState<CustomIconModel>
) {
    val size = remember { mutableStateOf(IntSize.Zero) }
    val itemsPerRow = remember(size.value.width) {
        if (size.value.width == 0) 1 else size.value.width / (50 * 3)
    }
    val totalItems = icons.size
    val remainder = totalItems % itemsPerRow

    val baseModifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .fillMaxWidth()
        .onSizeChanged { size.value = it }
        .border(1.dp, Dark10, RoundedCornerShape(16.dp))
        .padding(vertical = 15.dp, horizontal = 10.dp)
        .animateContentSize()


    FlowRow(
        modifier = baseModifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        val iconModifierBase = Modifier
            .padding(5.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Light60)
            .size(38.dp)


        icons.forEach { icon ->
            val isSelected = selectedIcon.value.id == icon.id
            val selectedModifier = if (isSelected) Modifier.border(
                1.dp, Violet80, RoundedCornerShape(8.dp)
            ) else Modifier

            AsyncImage(
                model = icon.path,
                contentDescription = null,
                placeholder = painterResource(R.drawable.ic_loading),
                modifier = iconModifierBase
                    .clickable(interactionSource = null, indication = null) {
                        selectedIcon.value = icon
                    }
                    .then(selectedModifier)
                    .padding(7.dp),
            )
        }
        if (remainder != 0) {
            repeat(itemsPerRow - remainder) {
                Spacer(
                    modifier = Modifier
                        .padding(5.dp)
                        .size(38.dp),
                )
            }
        }
    }
}
package com.memeusix.ekspensify.ui.dashboard.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.memeusix.ekspensify.R
import com.memeusix.ekspensify.components.DrawableEndText
import com.memeusix.ekspensify.components.VerticalSpace
import com.memeusix.ekspensify.data.model.requestModel.InsightsQueryModel
import com.memeusix.ekspensify.data.model.responseModel.CategoryInsightsResponseModel
import com.memeusix.ekspensify.ui.dashboard.transactions.data.getFormattedDateRange
import com.memeusix.ekspensify.ui.theme.Dark15
import com.memeusix.ekspensify.ui.theme.extendedColors
import com.memeusix.ekspensify.utils.DateFormat
import com.memeusix.ekspensify.utils.formatZonedDateTime
import com.memeusix.ekspensify.utils.roundedBorder

@Composable
fun CategoryInsightCard(
    selectedType: String,
    onClick: () -> Unit,
    query: InsightsQueryModel,
    categoryInsights: List<CategoryInsightsResponseModel>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Dark15.copy(alpha = 0.6f))
            .padding(16.dp)
    ) {
        Column {
            CategoryHeaderRow(
                selectedType = selectedType,
                onClick = onClick
            )
            VerticalSpace(10.dp)
            PieChartRow(categoryInsights, query)
        }
    }
}

@Composable
private fun CategoryHeaderRow(selectedType: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            stringResource(R.string.breakdown),
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.SemiBold,
            )
        )
        DrawableEndText(
            text = selectedType,
            icon = R.drawable.ic_arrow_down,
            iconSize = 22.dp,
            textSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.extendedColors.iconColor,
            modifier = Modifier
                .fillMaxHeight()
                .roundedBorder(40.dp)
                .clip(RoundedCornerShape(40))
                .clickable(onClick = onClick)
                .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
        )
    }
}


@Composable
private fun PieChartRow(
    categoryList: List<CategoryInsightsResponseModel>,
    query: InsightsQueryModel
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (categoryList.isEmpty()) {
            Column(
                Modifier.size(150.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    rememberAsyncImagePainter(R.drawable.img_empty_chart),
                    contentDescription = null,
                )
                VerticalSpace(15.dp)
                Text("No Date Found!", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            Chart(categoryList)
        }
        StartEndDateColumn(query)
    }
}

@Composable
private fun StartEndDateColumn(query: InsightsQueryModel) {
    val (startDateTime, endDateTime) = query.dateRange.getFormattedDateRange()
    Column(
        horizontalAlignment = Alignment.End,
    ) {
        println(query.dateRange.getFormattedDateRange())
        DateLabel("from", startDateTime.formatZonedDateTime(DateFormat.dd_MMM_yyyy_))
        VerticalSpace(20.dp)
        DateLabel("to", endDateTime.formatZonedDateTime(DateFormat.dd_MMM_yyyy_))
    }
}

@Composable
private fun DateLabel(label: String, date: String) {
    Text(
        label,
        style = MaterialTheme.typography.labelSmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
    VerticalSpace(8.dp)
    Text(
        date,
        style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Medium
        ),
    )
}
package com.memeusix.budgetbuddy.ui.dashboard.transactions

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.memeusix.budgetbuddy.components.AppBar
import com.memeusix.budgetbuddy.components.CustomDatePicker
import com.memeusix.budgetbuddy.components.FilledButton
import com.memeusix.budgetbuddy.components.VerticalSpace
import com.memeusix.budgetbuddy.data.model.requestModel.TransactionPaginationRequestModel
import com.memeusix.budgetbuddy.data.model.responseModel.AccountResponseModel
import com.memeusix.budgetbuddy.data.model.responseModel.CategoryResponseModel
import com.memeusix.budgetbuddy.ui.dashboard.transactions.data.AmountRange
import com.memeusix.budgetbuddy.ui.dashboard.transactions.data.DateRange
import com.memeusix.budgetbuddy.ui.dashboard.transactions.data.FilterOptions
import com.memeusix.budgetbuddy.ui.dashboard.transactions.data.FilterType
import com.memeusix.budgetbuddy.ui.dashboard.transactions.data.SelectedFilterModel
import com.memeusix.budgetbuddy.ui.dashboard.transactions.data.SortBy
import com.memeusix.budgetbuddy.ui.dashboard.transactions.data.getFormattedDateRange
import com.memeusix.budgetbuddy.ui.dashboard.transactions.filterComponets.CustomFilterTextField
import com.memeusix.budgetbuddy.ui.dashboard.transactions.filterComponets.FilterTitleItem
import com.memeusix.budgetbuddy.ui.dashboard.transactions.filterComponets.FilterValuesList
import com.memeusix.budgetbuddy.ui.dashboard.transactions.viewmodel.TransactionViewModel
import com.memeusix.budgetbuddy.ui.theme.Red100
import com.memeusix.budgetbuddy.ui.theme.extendedColors
import com.memeusix.budgetbuddy.utils.SpUtils
import com.memeusix.budgetbuddy.utils.TransactionType
import com.memeusix.budgetbuddy.utils.dynamicPadding
import com.memeusix.budgetbuddy.utils.singleClick
import com.memeusix.budgetbuddy.utils.spacedByWithFooter
import com.memeusix.budgetbuddy.utils.toggle
import java.io.Serializable
import java.time.LocalDate


@SuppressLint("NewApi")
@Composable
fun FilterScreen(
    navController: NavHostController, transactionViewModel: TransactionViewModel
) {

    val filterOptions by transactionViewModel.getFilterOptions.collectAsStateWithLifecycle()

    var selectedFilter by remember { mutableStateOf(filterOptions.first()) }

    val selectedFilterState = remember { mutableStateOf(SelectedFilterModel()) }

    val showDateRangePicker = remember { mutableStateOf(false to true) }
    val minAmtState = remember { mutableStateOf("") }
    val maxAmtState = remember { mutableStateOf("") }
    val startDateState = remember { mutableStateOf<LocalDate?>(null) }
    val endDateState = remember { mutableStateOf<LocalDate?>(null) }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val spUtils = SpUtils(context)
        transactionViewModel.initialize(spUtils)

        selectedFilterState.value =
            transactionViewModel.selectedFilterModel.value ?: SelectedFilterModel()

        if (selectedFilterState.value.amtRange == AmountRange.CUSTOM) {
            minAmtState.value =
                transactionViewModel.filterState.value.minAmount?.toString().orEmpty()
            maxAmtState.value =
                transactionViewModel.filterState.value.maxAmount?.toString().orEmpty()
        }
        if (selectedFilterState.value.dateRange == DateRange.CUSTOM) {
            startDateState.value = transactionViewModel.selectedFilterModel.value?.startDate
            startDateState.value = transactionViewModel.selectedFilterModel.value?.startDate
        }
    }

    if (showDateRangePicker.value.first) {
        CustomDatePicker(datePickerState = showDateRangePicker,
            initialDate = if (showDateRangePicker.value.second) startDateState.value else endDateState.value,
            onDateSelected = { date ->
                if (showDateRangePicker.value.second)
                    startDateState.value = date
                else
                    endDateState.value = date
            })
    }

    Scaffold(topBar = {
        AppBar(
            heading = "Filters",
            elevation = false,
            navController = navController,
            isBackNavigation = true
        )
    }) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .dynamicPadding(paddingValues)
                .padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        1.dp,
                        MaterialTheme.extendedColors.primaryBorder,
                        RoundedCornerShape(16.dp)
                    )
            ) {
                FilterTitleListView(
                    filterOptions,
                    selectedFilter,
                    selectedFilterState,
                    modifier = Modifier
                        .weight(3.5f)
                        .fillMaxHeight(),
                    onClick = {
                        selectedFilter = it
                    },
                    onReset = {
                        selectedFilterState.value = SelectedFilterModel()
                        minAmtState.value = ""
                        maxAmtState.value = ""
                        startDateState.value = null
                        endDateState.value = null
                    })
                VerticalDivider(color = MaterialTheme.extendedColors.primaryBorder)
                FilterValueListView(
                    selectedFilter,
                    selectedFilterState,
                    startDateState,
                    showDateRangePicker,
                    endDateState,
                    minAmtState,
                    maxAmtState,
                    Modifier.weight(5f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
            ) {
                FilledButton(
                    text = "Cancel",
                    onClick = singleClick {
                        navController.navigateUp()
                    },
                    modifier = Modifier.weight(1f),
                    textModifier = Modifier.padding(vertical = 17.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.extendedColors.imageBg,
                        contentColor = MaterialTheme.extendedColors.iconColor
                    )
                )
                FilledButton(
                    text = "Apply",
                    onClick = singleClick {
                        val requestModel = TransactionPaginationRequestModel(
                            type = selectedFilterState.value.type?.toString(),
                            accountIds = selectedFilterState.value.accounts.mapNotNull { it.id }
                                .takeIf { it.isNotEmpty() },
                            categoryIds = selectedFilterState.value.categories.mapNotNull { it.id }
                                .takeIf { it.isNotEmpty() },
                            minAmount = selectedFilterState.value.amtRange?.amountRange?.let { (min, max) ->
                                if (min == null && max == null) minAmtState.value.toIntOrNull() else min
                            },
                            maxAmount = selectedFilterState.value.amtRange?.amountRange?.let { (min, max) ->
                                if (min == null && max == null) maxAmtState.value.toIntOrNull() else max
                            },
                            startDate = selectedFilterState.value.dateRange?.getFormattedDateRange()
                                ?.let { (start, end) ->
                                    if (start == null && end == null) startDateState.value?.atStartOfDay()
                                        ?.toString() else start
                                },
                            endDate = selectedFilterState.value.dateRange?.getFormattedDateRange()
                                ?.let { (start, end) ->
                                    if (start == null && end == null) endDateState.value?.atTime(
                                        11,
                                        59,
                                        59
                                    )?.toString() else end
                                },
                            sort = selectedFilterState.value.sortBy?.sortBy
                            )
                        transactionViewModel.updateSelectedFilter(selectedFilterState.value)
                        transactionViewModel.updateFilter(requestModel)
                        navController.popBackStack()
                    },
                    textModifier = Modifier.padding(vertical = 17.dp),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

}

@Composable
fun FilterTitleListView(
    filterOptions: List<FilterOptions<out Serializable>>,
    selectedFilter: FilterOptions<out Serializable>,
    selectedFilterState: MutableState<SelectedFilterModel>,
    modifier: Modifier,
    onClick: (FilterOptions<out Serializable>) -> Unit,
    onReset: () -> Unit
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = spacedByWithFooter(0.dp)
    ) {
        item {
            VerticalSpace(16.dp)
        }
        items(
            filterOptions
        ) { item ->
            val filter = item.copy(
                isSelected = item.title == selectedFilter.title,
                isApplied = when (item.listType) {
                    FilterType.TYPE -> selectedFilterState.value.type != null
                    FilterType.DATE_RANGE -> selectedFilterState.value.dateRange != null
                    FilterType.AMT_RANGE -> selectedFilterState.value.amtRange != null
                    FilterType.CATEGORIES -> selectedFilterState.value.categories.isNotEmpty()
                    FilterType.ACCOUNTS -> selectedFilterState.value.accounts.isNotEmpty()
                    FilterType.SORT_BY -> selectedFilterState.value.sortBy != null
                }
            )
            FilterTitleItem(filter, onClick = { onClick(item) })
            HorizontalDivider(
                color = MaterialTheme.extendedColors.primaryBorder,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        item {
            Text(
                "Reset",
                style = MaterialTheme.typography.bodyMedium.copy(color = Red100),
                modifier = Modifier
                    .padding(start = 16.dp, bottom = 20.dp)
                    .clickable(onClick = onReset)
            )
        }
    }
}

@Composable
private fun FilterValueListView(
    selectedFilter: FilterOptions<out Serializable>,
    selectedFilterState: MutableState<SelectedFilterModel>,
    startDateState: MutableState<LocalDate?>,
    showDateRangePicker: MutableState<Pair<Boolean, Boolean>>,
    endDateState: MutableState<LocalDate?>,
    minAmtState: MutableState<String>,
    maxAmtState: MutableState<String>,
    modifier: Modifier
) {
    val isDateRangeCustom =
        rememberUpdatedState(selectedFilterState.value.dateRange == DateRange.CUSTOM)
    val isAmtRangeCustom =
        rememberUpdatedState(selectedFilterState.value.amtRange == AmountRange.CUSTOM)

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        item { VerticalSpace(8.dp) }

        items(selectedFilter.value) { item ->
            val isSelected = rememberUpdatedState(
                isSelected(item, selectedFilter, selectedFilterState)
            )
            FilterValuesList(item = item,
                isSelected = isSelected.value,
                onClick = { filterSelectionMethod(item, selectedFilter, selectedFilterState) })
        }

        when (selectedFilter.listType) {
            FilterType.DATE_RANGE -> if (isDateRangeCustom.value) {
                item {
                    CustomFilterTextField(
                        state = startDateState.value?.toString().orEmpty(),
                        isEnable = false,
                        onChange = {
                            showDateRangePicker.value = showDateRangePicker.value.copy(
                                first = true, second = true
                            )
                        },
                        placeHolder = "⏰ Start Date",

                        )
                }
                item {
                    CustomFilterTextField(state = endDateState.value?.toString().orEmpty(),
                        isEnable = false,
                        placeHolder = "⏰ End Date",
                        onChange = {
                            showDateRangePicker.value = showDateRangePicker.value.copy(
                                first = true, second = false
                            )
                        })
                }
            }

            FilterType.AMT_RANGE -> if (isAmtRangeCustom.value) {
                item {
                    CustomFilterTextField(
                        state = minAmtState.value,
                        isEnable = true,
                        placeHolder = "₹ Min Amount",
                        onChange = {
                            if (it.isDigitsOnly()) {
                                minAmtState.value = it
                            }
                        },
                    )
                }
                item {
                    CustomFilterTextField(state = maxAmtState.value,
                        isEnable = true,
                        placeHolder = "₹ Max Amount",
                        onChange = {
                            if (it.isDigitsOnly()) {
                                maxAmtState.value = it
                            }
                        })
                }
            }

            else -> Unit
        }
    }
}

private fun filterSelectionMethod(
    item: Serializable,
    selectedFilter: FilterOptions<out Serializable>,
    selectedFilterState: MutableState<SelectedFilterModel>
) {
    val currentSelection = selectedFilterState.value
    when (selectedFilter.listType) {
        FilterType.TYPE -> {
            selectedFilterState.value = currentSelection.copy(
                type = if (currentSelection.type == item as TransactionType) null else item
            )
        }

        FilterType.DATE_RANGE -> {
            selectedFilterState.value = currentSelection.copy(
                dateRange = if (currentSelection.dateRange == item as DateRange) null else item
            )
        }

        FilterType.AMT_RANGE -> {
            selectedFilterState.value = currentSelection.copy(
                amtRange = if (currentSelection.amtRange == item as AmountRange) null else item
            )
        }

        FilterType.CATEGORIES -> {
            val categories = currentSelection.categories.toMutableList()
            categories.toggle(item as CategoryResponseModel)
            selectedFilterState.value = currentSelection.copy(
                categories = categories
            )
        }

        FilterType.ACCOUNTS -> {
            val accounts = currentSelection.accounts.toMutableList()
            accounts.toggle(item as AccountResponseModel)
            selectedFilterState.value = currentSelection.copy(
                accounts = accounts
            )
        }

        FilterType.SORT_BY -> {
            selectedFilterState.value = currentSelection.copy(
                sortBy = if (currentSelection.sortBy == item as SortBy) null else item
            )
        }
    }
}

private fun isSelected(
    item: Serializable,
    selectedFilter: FilterOptions<out Serializable>,
    selectedFilterState: MutableState<SelectedFilterModel>
): Boolean {
    return when (selectedFilter.listType) {
        FilterType.TYPE -> selectedFilterState.value.type == item as TransactionType
        FilterType.CATEGORIES -> selectedFilterState.value.categories.any { it.id == (item as CategoryResponseModel).id }
        FilterType.ACCOUNTS -> selectedFilterState.value.accounts.any { it.id == (item as AccountResponseModel).id }
        FilterType.SORT_BY -> selectedFilterState.value.sortBy == item as SortBy
        FilterType.DATE_RANGE -> selectedFilterState.value.dateRange == item as DateRange
        FilterType.AMT_RANGE -> selectedFilterState.value.amtRange == item as AmountRange
    }
}


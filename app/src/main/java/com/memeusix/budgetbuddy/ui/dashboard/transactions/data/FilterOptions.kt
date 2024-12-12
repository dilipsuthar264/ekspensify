package com.memeusix.budgetbuddy.ui.dashboard.transactions.data

import com.memeusix.budgetbuddy.data.model.responseModel.AccountResponseModel
import com.memeusix.budgetbuddy.data.model.responseModel.CategoryResponseModel
import com.memeusix.budgetbuddy.utils.SpUtils
import com.memeusix.budgetbuddy.utils.TransactionType
import java.time.LocalDate

data class FilterOptions<T>(
    val title: String,
    val value: List<T> = emptyList(),
    val isMultiSelection: Boolean = false,
    val isSelected: Boolean = false,
    val isApplied: Boolean = false,
    val listType: FilterType
)

data class SelectedFilterModel(
    val type: TransactionType? = null,
    val dateRange: DateRange? = null,
    val amtRange: AmountRange? = null,
    val categories: List<CategoryResponseModel> = emptyList(),
    val accounts: List<AccountResponseModel> = emptyList(),
    val sortBy: SortBy? = null,
    val startDate : LocalDate? = null,
    val endDate : LocalDate? = null
)

enum class FilterType {
    TYPE,
    DATE_RANGE,
    AMT_RANGE,
    CATEGORIES,
    ACCOUNTS,
    SORT_BY,
}

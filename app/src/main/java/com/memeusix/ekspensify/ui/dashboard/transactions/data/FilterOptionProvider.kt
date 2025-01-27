package com.memeusix.ekspensify.ui.dashboard.transactions.data

import com.memeusix.ekspensify.data.model.responseModel.AccountResponseModel
import com.memeusix.ekspensify.data.model.responseModel.CategoryResponseModel
import com.memeusix.ekspensify.utils.TransactionType
import com.memeusix.ekspensify.utils.spUtils.SpUtilsManager


object FilterOptionProvider {
    // Late-initialized values for dynamic options
    private var categories: List<CategoryResponseModel>? = null
    private var accounts: List<AccountResponseModel>? = null

    fun initialize(spUtils: SpUtilsManager) {
        categories = spUtils.categoriesData.value?.categories
        accounts = spUtils.accountData.value?.accounts
    }

    fun getFilterOptions() = listOf(
        FilterOptions(
            "Type",
            TransactionType.entries,
            isMultiSelection = false,
            listType = FilterType.TYPE
        ),
        FilterOptions(
            "Amount",
            AmountRange.entries,
            isMultiSelection = false,
            listType = FilterType.AMT_RANGE
        ),
        FilterOptions(
            "Account",
            accounts.orEmpty(),
            isMultiSelection = true,
            listType = FilterType.ACCOUNTS
        ),
        FilterOptions(
            "Category",
            categories.orEmpty(),
            isMultiSelection = true,
            listType = FilterType.CATEGORIES
        ),
        FilterOptions(
            "Time Period",
            DateRange.entries,
            isMultiSelection = false,
            listType = FilterType.DATE_RANGE
        ),
        FilterOptions(
            "Sort By",
            SortBy.entries,
            isMultiSelection = false,
            listType = FilterType.SORT_BY
        )
    )
}


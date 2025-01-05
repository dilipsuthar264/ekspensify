package com.memeusix.budgetbuddy.ui.dashboard.transactions

import android.util.Log
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.memeusix.budgetbuddy.R
import com.memeusix.budgetbuddy.components.EmptyView
import com.memeusix.budgetbuddy.components.PagingListLoader
import com.memeusix.budgetbuddy.components.PullToRefreshLayout
import com.memeusix.budgetbuddy.data.model.responseModel.TransactionResponseModel
import com.memeusix.budgetbuddy.navigation.FilterScreenRoute
import com.memeusix.budgetbuddy.ui.dashboard.transactions.components.TransactionListItem
import com.memeusix.budgetbuddy.ui.dashboard.transactions.data.SelectedFilterModel
import com.memeusix.budgetbuddy.ui.dashboard.transactions.filterComponets.SearchBar
import com.memeusix.budgetbuddy.ui.dashboard.transactions.viewmodel.TransactionViewModel
import com.memeusix.budgetbuddy.ui.theme.extendedColors
import com.memeusix.budgetbuddy.utils.NavigationRequestKeys
import com.memeusix.budgetbuddy.utils.handleApiResponse
import com.memeusix.budgetbuddy.utils.singleClick
import com.memeusix.budgetbuddy.utils.toastUtils.CustomToastModel

@Composable
fun TransactionScreen(
    navController: NavHostController,
    transactionViewModel: TransactionViewModel,
) {
    val transactions = transactionViewModel.getTransactions().collectAsLazyPagingItems()


    val dialogState = transactionViewModel.transactionDetails.collectAsStateWithLifecycle()
    val showDialog = rememberUpdatedState(dialogState.value != null)
    val deleteTransaction by transactionViewModel.deleteTransaction.collectAsStateWithLifecycle()
    val toastState = remember { mutableStateOf<CustomToastModel?>(null) }
    val searchState = remember { mutableStateOf("") }

    val selectedFilterModel by transactionViewModel.userSelectedFilters.collectAsStateWithLifecycle()

    LaunchedEffect(deleteTransaction) {
        Log.e("TransactionScreen", "TransactionScreen: ${transactionViewModel.transactionQueryParameters.value}")
        handleApiResponse(
            response = deleteTransaction,
            toastState = toastState,
            navController = navController,
            onSuccess = {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    NavigationRequestKeys.REFRESH_TRANSACTION, true
                )
            }
        )
    }
    if (showDialog.value) {
        TransactionDetailsDialog(
            transaction = dialogState.value,
            navController = navController,
            onDeleteClick = { id ->
                id?.let {
                    transactionViewModel.deleteTransaction(it)
                    transactionViewModel.closeDialog()
                }
            },
            onDismiss = {
                transactionViewModel.closeDialog()
            })
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SearchBar(
            searchState = searchState,
            onSearch = {
                transactionViewModel.updateTransactionQuery(transactionViewModel.transactionQueryParameters.value.copy(
                    q = searchState.value.ifEmpty { null }
                ))
            },
            onFilterClick = singleClick {
                navController.navigate(
                    FilterScreenRoute
                )
            },
            isFilterApplied = selectedFilterModel != SelectedFilterModel()
        )
        PullToRefreshLayout(
            onRefresh = transactionViewModel::refreshTransaction,
            modifier = Modifier.weight(1f)
        ) {
            if (transactions.itemCount == 0 && transactions.loadState.isIdle) {
                EmptyView(
                    title = stringResource(R.string.no_transactions_found),
                    description = stringResource(R.string.there_s_nothing_here_at_the_moment_create_a_transaction_to_continue_tracking)
                )
            }
            TransactionListContent(
                modifier = Modifier.fillMaxSize(),
                transactions = transactions,
                onClick = { transaction ->
                    transactionViewModel.openDialog(transaction)
                }
            )
        }
    }
}


@Composable
fun TransactionListContent(
    modifier: Modifier,
    transactions: LazyPagingItems<TransactionResponseModel>,
    isEnable: Boolean = true,
    onClick: (TransactionResponseModel?) -> Unit,
) {
    val lazyState = rememberLazyListState()
    LazyColumn(
        modifier = modifier,
        state = lazyState,
        flingBehavior = ScrollableDefaults.flingBehavior(),
        userScrollEnabled = true
    ) {
        items(transactions.itemCount, key = transactions.itemKey { it.id!! }) { index ->
            val transaction = transactions[index]
            TransactionListItem(
                transaction = transaction,
                onClick = onClick,
                isEnable = isEnable,
                modifier = Modifier.padding(
                    vertical = 15.dp,
                    horizontal = 20.dp
                ),
            )
            HorizontalDivider(
                color = MaterialTheme.extendedColors.primaryBorder
            )
        }
        transactions.apply {
            item {
                PagingListLoader(loadState)
            }
        }
    }
}

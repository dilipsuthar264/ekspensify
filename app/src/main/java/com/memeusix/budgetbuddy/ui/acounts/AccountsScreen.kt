package com.memeusix.budgetbuddy.ui.acounts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastSumBy
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.memeusix.budgetbuddy.R
import com.memeusix.budgetbuddy.components.AppBar
import com.memeusix.budgetbuddy.components.CustomListItem
import com.memeusix.budgetbuddy.components.EmptyAccountsView
import com.memeusix.budgetbuddy.components.FilledButton
import com.memeusix.budgetbuddy.components.PullToRefreshLayout
import com.memeusix.budgetbuddy.components.ShowLoader
import com.memeusix.budgetbuddy.components.VerticalSpace
import com.memeusix.budgetbuddy.data.ApiResponse
import com.memeusix.budgetbuddy.data.model.responseModel.AccountResponseModel
import com.memeusix.budgetbuddy.navigation.AccountScreenRoute
import com.memeusix.budgetbuddy.navigation.CreateAccountScreenRoute
import com.memeusix.budgetbuddy.ui.acounts.components.AccountBalance
import com.memeusix.budgetbuddy.ui.acounts.components.AccountCardFooter
import com.memeusix.budgetbuddy.ui.acounts.components.AccountIcon
import com.memeusix.budgetbuddy.ui.acounts.components.AccountsCardView
import com.memeusix.budgetbuddy.ui.acounts.components.AmountText
import com.memeusix.budgetbuddy.ui.acounts.viewModel.AccountViewModel
import com.memeusix.budgetbuddy.ui.theme.extendedColors
import com.memeusix.budgetbuddy.utils.AccountType
import com.memeusix.budgetbuddy.utils.dynamicImePadding
import com.memeusix.budgetbuddy.utils.formatRupees
import com.memeusix.budgetbuddy.utils.getViewModelStoreOwner
import com.memeusix.budgetbuddy.utils.singleClick
import com.memeusix.budgetbuddy.utils.toJson

@Composable
fun AccountsScreen(
    navController: NavHostController,
    args: AccountScreenRoute,
    viewModel: AccountViewModel = hiltViewModel(navController.getViewModelStoreOwner())
) {
    val getAllAccounts by viewModel.getAllAccounts.collectAsStateWithLifecycle()
    val isLoading = getAllAccounts is ApiResponse.Loading

    val selectedAccountType = remember { mutableStateOf(AccountType.BANK) }


    LaunchedEffect(Unit) {
        // Check if the response contains bank accounts and set the selectedAccountType accordingly
        if (getAllAccounts is ApiResponse.Success) {
            val bankAccountsExist =
                getAllAccounts.data?.any { it.type == AccountType.BANK.toString() } == true
            selectedAccountType.value =
                if (bankAccountsExist) AccountType.BANK else AccountType.WALLET
        }
    }

    // Main Ui
    Scaffold(
        topBar = {
            AppBar(
                heading = stringResource(R.string.accounts),
                navController = navController,
                isBackNavigation = args.isFromProfile,
            )
        },
    ) { paddingValues ->
        PullToRefreshLayout(
            onRefresh = viewModel::getAllAccounts,
            modifier = Modifier
                .fillMaxSize()
                .dynamicImePadding(paddingValues),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                getAllAccounts.data?.let { accountList ->
                    if (accountList.isEmpty()) {
                        EmptyAccountsView(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                            onClick = singleClick {
                                goToCreateAccount(navController, args, accountList)
                            }
                        )
                    } else {
                        TotalBalance(
                            accountList.fastSumBy { it.balance ?: 0 }.toString(),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        VerticalSpace(38.dp)
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            AccountCard(selectedAccountType, accountList, navController, args)
                        }
                        AddMoreBtn(args) {
                            goToCreateAccount(navController, args, accountList)
                        }
                    }
                }
            }
        }
        // Loader
        ShowLoader(isLoading)
    }
}

@Composable
private fun TotalBalance(
    balance: String, modifier: Modifier
) {
    AmountText(balance, modifier)
    VerticalSpace(8.dp)
    Text(
        text = stringResource(R.string.total_balance),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}


@Composable
private fun AccountCard(
    selectedAccountType: MutableState<AccountType>,
    accountList: List<AccountResponseModel>,
    navController: NavController,
    args: AccountScreenRoute
) {
    AccountsCardView(
        selectedAccountType = selectedAccountType.value,
        onTypeChange = {
            selectedAccountType.value = it
        }
    ) {
        AccountList(
            accountList,
            selectedAccountType.value,
            modifier = Modifier.weight(1f, false)
        ) { item ->
            navController.navigate(
                CreateAccountScreenRoute(
                    accountResponseArgs = item.toJson(),
                    accountList = accountList.toJson(),
                    isFromProfile = args.isFromProfile
                )
            )
        }
        AccountCardFooter(accountList, selectedAccountType.value)
    }
}

@Composable
private fun AccountList(
    accountList: List<AccountResponseModel>,
    selectedAccountType: AccountType,
    modifier: Modifier = Modifier,
    onClick: (AccountResponseModel) -> Unit,
) {
    val filteredList = remember(accountList, selectedAccountType) {
        accountList.filter { it.type == selectedAccountType.toString() }
    }
    LazyColumn(
        modifier = modifier,
    ) {
        items(
            filteredList,
            key = { it.id!! }
        ) { account ->
            CustomListItem(
                leadingContent = {
                    AccountIcon(account)
                },
                title = account.name.orEmpty(),
                modifier = Modifier.padding(10.dp),
                trailingContent = {
                    AccountBalance(true, account.balance?.formatRupees() ?: "0")
                },
                onClick = singleClick { onClick(account) }
            )
            HorizontalDivider(color = MaterialTheme.extendedColors.primaryBorder)
        }
    }
}

@Composable
private fun AddMoreBtn(args: AccountScreenRoute, onClick: () -> Unit) {
    val text =
        if (args.isFromProfile) stringResource(R.string.add) else stringResource(R.string.continue_)
    FilledButton(text = text,
        textModifier = Modifier.padding(vertical = 17.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = singleClick { onClick() })
}


private fun goToCreateAccount(
    navController: NavController,
    args: AccountScreenRoute,
    accountList: List<AccountResponseModel>,
) {
    navController.navigate(
        CreateAccountScreenRoute(
            accountResponseArgs = null,
            accountList = accountList.toJson(),
            isFromProfile = args.isFromProfile
        )
    )
}

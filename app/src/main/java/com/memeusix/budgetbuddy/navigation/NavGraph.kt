package com.memeusix.budgetbuddy.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.memeusix.budgetbuddy.data.model.requestModel.AuthRequestModel
import com.memeusix.budgetbuddy.ui.SplashScreen
import com.memeusix.budgetbuddy.ui.acounts.AccountsScreen
import com.memeusix.budgetbuddy.ui.acounts.CreateAccountScreen
import com.memeusix.budgetbuddy.ui.auth.IntroScreen
import com.memeusix.budgetbuddy.ui.auth.LoginScreen
import com.memeusix.budgetbuddy.ui.auth.OtpVerificationScreen
import com.memeusix.budgetbuddy.ui.auth.RegisterScreen
import com.memeusix.budgetbuddy.ui.categories.CategoriesScreen
import com.memeusix.budgetbuddy.ui.categories.CreateCategoryScreen
import com.memeusix.budgetbuddy.ui.categories.viewmodel.CategoryViewModel
import com.memeusix.budgetbuddy.ui.dashboard.bottomNav.BottomNav
import com.memeusix.budgetbuddy.ui.dashboard.transactions.CreateTransactionScreen
import com.memeusix.budgetbuddy.ui.dashboard.transactions.FilterScreen
import com.memeusix.budgetbuddy.ui.dashboard.transactions.viewmodel.TransactionViewModel


@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = SplashScreenRoute,
        enterTransition = {
            fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            fadeOut(
                animationSpec = tween(300)
            )
        },
        exitTransition = { ExitTransition.KeepUntilTransitionsFinished },
        popEnterTransition = { EnterTransition.None },
    ) {
        composable<SplashScreenRoute> {
            SplashScreen(navController)
        }
        composable<IntroScreenRoute> {
            IntroScreen(navController)
        }
        composable<LoginScreenRoute> {
            LoginScreen(navController = navController)
        }
        composable<RegisterScreenRoute> {
            RegisterScreen(navController = navController)
        }
        composable<OtpVerificationScreenRoute> {
            val args = it.toRoute<OtpVerificationScreenRoute>()
            val authRequestModel = AuthRequestModel(
                name = args.name,
                email = args.email
            )
            OtpVerificationScreen(
                navController = navController,
                navArgs = authRequestModel,
            )
        }


        // Bottom Nav Screen
        composable<BottomNavRoute> {
            val viewmodel: TransactionViewModel =
                hiltViewModel(navController.getViewModelStoreOwner(navController.graph.id))
            BottomNav(navController = navController, transactionViewModel = viewmodel)
        }


        // Account Screens
        composable<AccountScreenRoute>(
        ) {
            val args = it.toRoute<AccountScreenRoute>()
            AccountsScreen(navController, args)
        }
        composable<CreateAccountScreenRoute> {
            val args = it.toRoute<CreateAccountScreenRoute>()
            CreateAccountScreen(
                navController,
                args
            )
        }

        // Categories Screens
        composable<CategoriesScreenRoute> {
            CategoriesScreen(navController)
        }

        composable<CreateCategoryScreenRoute> {
            val parentEntry = navController.getBackStackEntry(CategoriesScreenRoute)
            val viewModel: CategoryViewModel = hiltViewModel(parentEntry)
            CreateCategoryScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        // Transaction Screens
        composable<CreateTransactionScreenRoute> {
            val args = it.toRoute<CreateTransactionScreenRoute>()
            CreateTransactionScreen(navController, args)
        }

        composable<FilterScreenRoute> {
            val viewmodel: TransactionViewModel =
                hiltViewModel(navController.getViewModelStoreOwner(navController.graph.id))
            FilterScreen(navController, viewmodel)
        }
    }
}



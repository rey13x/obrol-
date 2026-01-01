package com.bagas.obrol.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bagas.obrol.ui.AppViewModelProvider
import com.bagas.obrol.ui.screen.call.CallDestination
import com.bagas.obrol.ui.screen.call.CallScreen
import com.bagas.obrol.ui.screen.call.CallViewModelFactory
import com.bagas.obrol.ui.screen.chat.ChatDestination
import com.bagas.obrol.ui.screen.chat.ChatScreen
import com.bagas.obrol.ui.screen.chat.ChatViewModelFactory
import com.bagas.obrol.ui.screen.home.HomeDestination
import com.bagas.obrol.ui.screen.home.HomeScreen
import com.bagas.obrol.ui.screen.info.InfoDestination
import com.bagas.obrol.ui.screen.info.InfoScreen
import com.bagas.obrol.ui.screen.info.InfoViewModelFactory
import com.bagas.obrol.ui.screen.servicechat.ServiceChatDestination
import com.bagas.obrol.ui.screen.servicechat.ServiceChatScreen
import com.bagas.obrol.ui.screen.settings.SettingsDestination
import com.bagas.obrol.ui.screen.settings.SettingsScreen

@Composable
fun ObrolNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier,
        enterTransition = { fadeIn(animationSpec = tween(300)) },
        exitTransition = { fadeOut(animationSpec = tween(300)) },
        popEnterTransition = { fadeIn(animationSpec = tween(300)) },
        popExitTransition = { fadeOut(animationSpec = tween(300)) }
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                homeViewModel = viewModel(factory = AppViewModelProvider.Factory),
                onChatClick = { chat ->
                    if (chat.contact.isServiceContact) {
                        navController.navigate(ServiceChatDestination.route)
                    } else {
                        navController.navigate("${ChatDestination.route}/${chat.contact.account.accountId}")
                    }
                },
                onSettingsButtonClick = {
                    navController.navigate(SettingsDestination.route)
                }
            )
        }

        composable(
            route = ChatDestination.routeWithArgs,
            arguments = listOf(navArgument(ChatDestination.accountIdArg) { type = NavType.LongType })
        ) { backStackEntry ->
            val accountId = backStackEntry.arguments?.getLong(ChatDestination.accountIdArg) ?: -1L
            val factory = ChatViewModelFactory(accountId)
            ChatScreen(
                chatViewModel = viewModel(factory = factory),
                navController = navController
            )
        }

        composable(route = SettingsDestination.route) {
            SettingsScreen(
                settingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
                navController = navController
            )
        }

        composable(route = ServiceChatDestination.route) {
            ServiceChatScreen(
                navController = navController,
                viewModel = viewModel(factory = AppViewModelProvider.Factory)
            )
        }

        composable(
            route = InfoDestination.routeWithArgs,
            arguments = listOf(navArgument(InfoDestination.ACCOUNT_ID_ARG) { type = NavType.LongType })
        ) { backStackEntry ->
            val accountId = backStackEntry.arguments?.getLong(InfoDestination.ACCOUNT_ID_ARG) ?: -1L
            val factory = InfoViewModelFactory(accountId)
            InfoScreen(
                infoViewModel = viewModel(factory = factory),
                navController = navController
            )
        }

        composable(
            route = CallDestination.routeWithArgs,
            arguments = listOf(
                navArgument(CallDestination.accountIdArg) { type = NavType.LongType },
                navArgument(CallDestination.callStateArg) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val accountId = backStackEntry.arguments?.getLong(CallDestination.accountIdArg) ?: -1L
            val callState = backStackEntry.arguments?.getString(CallDestination.callStateArg) ?: ""
            val factory = CallViewModelFactory(accountId, callState)
            CallScreen(
                callViewModel = viewModel(factory = factory),
                navController = navController
            )
        }
    }
}

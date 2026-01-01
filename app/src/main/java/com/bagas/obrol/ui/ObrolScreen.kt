package com.bagas.obrol.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.bagas.obrol.ui.navigation.ObrolNavGraph

@Composable
fun ObrolScreen() {
    val navController = rememberNavController()
    ObrolNavGraph(navController = navController)
}

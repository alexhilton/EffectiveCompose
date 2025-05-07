package net.toughcoder.effectivecompose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object Destinations {
    const val APP_URI = "http://toughcoder.net/effectivecompose"
    const val HOME = "home";
    const val FLAG = "waving_flag"
    const val ANIM = "animations"
    const val GLES = "opengles"
}

object WavingFlag

object Animations

object OpenGLES

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    start: String = Destinations.HOME
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = start
    ) {
        composable(
            route = Destinations.HOME
        ) {
            HomeScreen { where ->
                navController.navigate(where)
            }
        }

        composable(route = Destinations.ANIM) {
            AnimateVisibility {
                navController.popBackStack()
            }
        }

        composable(route = Destinations.FLAG) {
            FiveStarScreen { navController.popBackStack() }
        }
    }
}
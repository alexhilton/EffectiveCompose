package net.toughcoder.effectivecompose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable

object Destinations {
    const val HOME = "home"
    const val ANIM = "animations"
    const val FLAG = "wavingflag"
    const val GLES = "oepngles"
}

@Serializable
object Home

@Serializable
object WavingFlag

@Serializable
object Animations

@Serializable
object OpenGLES

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    start: Home = Home
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = start
    ) {
        composable<Home> {
            HomeScreen { where ->
                val route = when (where) {
                    Destinations.ANIM -> Animations
                    Destinations.FLAG -> WavingFlag
                    Destinations.GLES -> OpenGLES
                    else -> Home
                }
                navController.navigate(route = route)
            }
        }

        composable<Animations> {
            AnimateVisibility {
                navController.popBackStack()
            }
        }

        composable<WavingFlag> {
            FiveStarScreen { navController.popBackStack() }
        }

        composable<OpenGLES> {
            GLESScreen { navController.popBackStack() }
        }
    }
}
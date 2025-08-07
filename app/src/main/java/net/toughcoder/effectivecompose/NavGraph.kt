package net.toughcoder.effectivecompose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.toughcoder.effectivecompose.shader.FirstShaderScreen
import net.toughcoder.effectivecompose.shader.FirstShader

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
                navController.navigate(route = where)
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

        composable<FirstShader> {
            FirstShaderScreen { navController.popBackStack() }
        }
    }
}
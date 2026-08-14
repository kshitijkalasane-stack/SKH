package com.example

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.GramVikasRepository
import com.example.ui.GramVikasViewModel
import com.example.ui.ViewModelFactory
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.projects.ProjectsScreen
import com.example.ui.projectdetails.ProjectDetailsScreen
import com.example.ui.contractors.ContractorsScreen

@Composable
fun GramVikasApp(repository: GramVikasRepository) {
    val navController = rememberNavController()
    val viewModel: GramVikasViewModel = viewModel(factory = ViewModelFactory(repository))

    LaunchedEffect(Unit) {
        viewModel.seedDatabaseIfEmpty()
    }

    NavHost(navController = navController, startDestination = "public_home") {
        composable("public_home") {
            com.example.ui.home.PublicHomeScreen(
                viewModel = viewModel,
                onLoginClick = { navController.navigate("login") },
                onReportIssueClick = { navController.navigate("report_issue") }
            )
        }
        composable("report_issue") {
            com.example.ui.camera.CameraCaptureScreen(
                onPhotoCaptured = { uri, location ->
                    viewModel.insertPublicIssue(
                        description = "Reported by Citizen via Photo Terminal",
                        photoUri = uri.toString(),
                        location = location
                    )
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() }
            )
        }
        composable("login") {
            com.example.ui.login.LoginScreen(
                onLoginSuccess = { 
                    navController.navigate("projects") {
                        popUpTo("public_home")
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable("dashboard") {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToProjects = { navController.navigate("projects") },
                onNavigateToContractors = { navController.navigate("contractors") },
                onProjectClick = { projectId -> navController.navigate("project_details/$projectId") }
            )
        }
        composable("projects") {
            ProjectsScreen(
                viewModel = viewModel,
                onNavigateToDashboard = { navController.navigate("dashboard") },
                onNavigateToContractors = { navController.navigate("contractors") },
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToIssues = { navController.navigate("issues") },
                onLogout = { navController.popBackStack("public_home", inclusive = false) },
                onProjectClick = { projectId -> navController.navigate("project_details/$projectId") }
            )
        }
        composable("issues") {
            com.example.ui.issues.PublicIssuesScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            "project_details/{projectId}",
            arguments = listOf(navArgument("projectId") { type = NavType.IntType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getInt("projectId") ?: return@composable
            ProjectDetailsScreen(
                projectId = projectId,
                viewModel = viewModel,
                onNavigateToCamera = { milestoneId -> navController.navigate("camera/$milestoneId") },
                onBack = { navController.popBackStack() },
                savedStateHandle = backStackEntry.savedStateHandle
            )
        }
        composable("contractors") {
            ContractorsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("settings") {
            com.example.ui.settings.SettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "camera/{milestoneId}",
            arguments = listOf(navArgument("milestoneId") { type = NavType.IntType })
        ) { backStackEntry ->
            val milestoneId = backStackEntry.arguments?.getInt("milestoneId") ?: return@composable
            com.example.ui.camera.CameraCaptureScreen(
                onPhotoCaptured = { uri, location ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("photoUri_${milestoneId}", uri.toString())
                    navController.previousBackStackEntry?.savedStateHandle?.set("location_${milestoneId}", location)
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() }
            )
        }
    }
}

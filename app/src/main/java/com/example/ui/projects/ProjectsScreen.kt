package com.example.ui.projects

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import com.example.data.ProjectWithDetails
import com.example.ui.GramVikasViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    viewModel: GramVikasViewModel,
    onNavigateToDashboard: () -> Unit,
    onNavigateToContractors: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToIssues: () -> Unit,
    onLogout: () -> Unit,
    onProjectClick: (Int) -> Unit
) {
    val projectsWithDetails by viewModel.allProjectsWithDetails.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showAddProjectDialog by remember { mutableStateOf(false) }

    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "In Progress", "Delayed", "Completed")

    val filteredProjects = remember(projectsWithDetails, selectedFilter) {
        if (selectedFilter == "All") {
            projectsWithDetails
        } else {
            projectsWithDetails.filter { it.project.status == selectedFilter }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(16.dp))
                Text("GramVikas Menu", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Project Progress") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Daily Report") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Assessment, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Daily Update by Camera") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.PhotoCamera, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Public Issues / Reports") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        onNavigateToIssues()
                    },
                    icon = { Icon(Icons.Default.Assessment, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                Spacer(modifier = Modifier.weight(1f))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Logout") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        onLogout()
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout") },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding).padding(bottom = 16.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Ongoing Projects", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { showAddProjectDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Add New Project")
                        }
                        IconButton(onClick = onNavigateToContractors) {
                            Icon(Icons.Default.Groups, contentDescription = "Contractors")
                        }
                        IconButton(onClick = onNavigateToDashboard) {
                            Icon(Icons.Default.Dashboard, contentDescription = "Dashboard")
                        }
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(if (filter == "In Progress") "On-Track" else filter) }
                    )
                }
            }

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.refreshProjects() },
                modifier = Modifier.weight(1f)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredProjects) { pwd ->
                        ProjectItemCard(pwd, onClick = { onProjectClick(pwd.project.id) })
                    }
                }
            }
        }
    }

    if (showAddProjectDialog) {
        AddProjectDialog(
            onDismiss = { showAddProjectDialog = false },
            onAdd = { name, location, desc, budget ->
                viewModel.addNewProject(name, location, desc, budget)
                showAddProjectDialog = false
            }
        )
    }
}
}

@Composable
fun AddProjectDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Project") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Project Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = budget,
                    onValueChange = { budget = it },
                    label = { Text("Planned Budget") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { 
                val budgetValue = budget.toDoubleOrNull() ?: 0.0
                onAdd(name, location, description, budgetValue) 
            }) {
                Text("Add Project")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ProjectItemCard(pwd: ProjectWithDetails, onClick: () -> Unit) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    val actualSpend = pwd.expenses.sumOf { it.amount }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(pwd.project.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Badge(
                    containerColor = when(pwd.project.status) {
                        "Delayed" -> MaterialTheme.colorScheme.error // Red
                        "Completed" -> MaterialTheme.colorScheme.primary
                        "In Progress" -> Color(0xFF4CAF50) // Green
                        else -> MaterialTheme.colorScheme.secondary
                    }
                ) {
                    Text(pwd.project.status, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Location: ${pwd.project.location}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Financial Progress", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { (actualSpend / pwd.project.plannedBudget).toFloat().coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
                color = if (actualSpend > pwd.project.plannedBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Spend: ${currencyFormatter.format(actualSpend)}", style = MaterialTheme.typography.bodySmall)
                Text("Budget: ${currencyFormatter.format(pwd.project.plannedBudget)}", style = MaterialTheme.typography.bodySmall)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Calculate ratios for Timeline vs Work
            val totalMilestones = pwd.milestones.size
            val completedMilestones = pwd.milestones.count { it.isCompleted }
            val workProgress = if (totalMilestones > 0) completedMilestones.toFloat() / totalMilestones else 0f
            
            val now = System.currentTimeMillis()
            val totalDuration = pwd.project.estimatedEndDate - pwd.project.startDate
            val elapsed = now - pwd.project.startDate
            val timeProgress = if (totalDuration > 0) (elapsed.toFloat() / totalDuration).coerceIn(0f, 1f) else 0f
            
            Text("Work vs Time Timeline", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            ProgressVsTimeBar(
                workProgress = workProgress,
                timeProgress = timeProgress,
                modifier = Modifier.fillMaxWidth().height(12.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Work: ${(workProgress * 100).roundToInt()}%", style = MaterialTheme.typography.bodySmall)
                Text("Time Elapsed: ${(timeProgress * 100).roundToInt()}%", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun ProgressVsTimeBar(
    workProgress: Float,
    timeProgress: Float,
    modifier: Modifier = Modifier
) {
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val onTrackColor = Color(0xFF4CAF50) // Green
    val delayedColor = MaterialTheme.colorScheme.error
    val timeMarkerColor = MaterialTheme.colorScheme.onSurface

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val cornerRadius = CornerRadius(height / 2, height / 2)

        // Draw background track
        drawRoundRect(
            color = trackColor,
            size = Size(width, height),
            cornerRadius = cornerRadius
        )

        // Draw work progress fill (Red if work is lagging behind time elapsed by more than 5%)
        val isDelayed = workProgress < (timeProgress - 0.05f)
        val fillColor = if (isDelayed) delayedColor else onTrackColor
        drawRoundRect(
            color = fillColor,
            size = Size(width * workProgress.coerceIn(0f, 1f), height),
            cornerRadius = cornerRadius
        )

        // Draw time elapsed marker
        val markerX = (width * timeProgress.coerceIn(0f, 1f)).coerceAtMost(width - 2.dp.toPx()).coerceAtLeast(2.dp.toPx())
        drawLine(
            color = timeMarkerColor,
            start = Offset(markerX, 0f),
            end = Offset(markerX, height),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

package com.example.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ProjectWithDetails
import com.example.ui.GramVikasViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: GramVikasViewModel,
    onNavigateToProjects: () -> Unit,
    onNavigateToContractors: () -> Unit,
    onProjectClick: (Int) -> Unit
) {
    val projectsWithDetails by viewModel.allProjectsWithDetails.collectAsStateWithLifecycle()
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GramVikas Dashboard", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SummaryCard(
                        title = "Total Projects",
                        value = projectsWithDetails.size.toString(),
                        icon = Icons.Default.AccountTree,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToProjects
                    )
                    
                    val delayedProjects = projectsWithDetails.count { it.project.status == "Delayed" }
                    SummaryCard(
                        title = "Delayed",
                        value = delayedProjects.toString(),
                        icon = Icons.Default.Warning,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.errorContainer,
                        onClick = onNavigateToProjects
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SummaryCard(
                        title = "Contractors",
                        value = "View All",
                        icon = Icons.Default.Groups,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToContractors
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Early Warning Alerts", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            val alerts = generateAlerts(projectsWithDetails)
            if (alerts.isEmpty()) {
                item {
                    Text("No alerts at this time. All projects are on track.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                items(alerts) { alert ->
                    AlertCard(alert = alert, onClick = { onProjectClick(alert.projectId) })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.secondaryContainer,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleMedium)
            }
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
    }
}

data class DashboardAlert(val projectId: Int, val projectName: String, val message: String, val severity: String) // "High", "Medium"

fun generateAlerts(projects: List<ProjectWithDetails>): List<DashboardAlert> {
    val alerts = mutableListOf<DashboardAlert>()
    val now = System.currentTimeMillis()

    for (p in projects) {
        val actualSpend = p.expenses.sumOf { it.amount }
        if (actualSpend > p.project.plannedBudget) {
            alerts.add(DashboardAlert(p.project.id, p.project.name, "Budget Overrun: Actual spend exceeds planned budget.", "High"))
        }

        val overdueMilestones = p.milestones.filter { !it.isCompleted && it.expectedCompletionDate < now }
        if (overdueMilestones.isNotEmpty()) {
            alerts.add(DashboardAlert(p.project.id, p.project.name, "${overdueMilestones.size} Overdue Milestone(s).", "Medium"))
        }
    }
    return alerts
}

@Composable
fun AlertCard(alert: DashboardAlert, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (alert.severity == "High") MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (alert.severity == "High") Icons.Default.Error else Icons.Default.Warning,
                contentDescription = null,
                tint = if (alert.severity == "High") MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onTertiaryContainer
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(alert.projectName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(alert.message, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

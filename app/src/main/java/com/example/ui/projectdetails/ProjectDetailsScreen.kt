package com.example.ui.projectdetails

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Milestone
import com.example.ui.GramVikasViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailsScreen(
    projectId: Int,
    viewModel: GramVikasViewModel,
    onNavigateToCamera: (Int) -> Unit,
    onBack: () -> Unit,
    savedStateHandle: androidx.lifecycle.SavedStateHandle
) {
    val projectWithDetails by viewModel.getProjectWithDetails(projectId).collectAsStateWithLifecycle()
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    LaunchedEffect(projectWithDetails) {
        if (projectWithDetails != null) {
            projectWithDetails?.milestones?.forEach { milestone ->
                val uriStr = savedStateHandle.get<String>("photoUri_${milestone.id}")
                val locStr = savedStateHandle.get<String>("location_${milestone.id}")
                if (uriStr != null) {
                    val updated = milestone.copy(photoUri = uriStr, locationCoordinates = locStr)
                    viewModel.updateMilestone(updated)
                    savedStateHandle.remove<String>("photoUri_${milestone.id}")
                    savedStateHandle.remove<String>("location_${milestone.id}")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(projectWithDetails?.project?.name ?: "Loading...") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (projectWithDetails == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val pwd = projectWithDetails!!
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Project Info", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Location: ${pwd.project.location}")
                            Text("Status: ${pwd.project.status}")
                            Text("Start Date: ${dateFormatter.format(Date(pwd.project.startDate))}")
                            Text("Est. End Date: ${dateFormatter.format(Date(pwd.project.estimatedEndDate))}")
                        }
                    }
                }

                item {
                    Text("Milestones", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }

                items(pwd.milestones) { milestone ->
                    MilestoneItem(
                        milestone = milestone,
                        onToggle = { 
                            val updated = milestone.copy(
                                isCompleted = !milestone.isCompleted, 
                                actualCompletionDate = if (!milestone.isCompleted) System.currentTimeMillis() else null
                            )
                            viewModel.updateMilestone(updated)
                        },
                        onCameraClick = { onNavigateToCamera(milestone.id) }
                    )
                }
                
                item { Spacer(modifier = Modifier.height(16.dp)) }

                item {
                    Text("Expenses & Budget", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                
                item {
                    val totalSpend = pwd.expenses.sumOf { it.amount }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Planned Budget:", fontWeight = FontWeight.Bold)
                                Text(currencyFormatter.format(pwd.project.plannedBudget))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Actual Spend:", fontWeight = FontWeight.Bold, color = if (totalSpend > pwd.project.plannedBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
                                Text(currencyFormatter.format(totalSpend), color = if (totalSpend > pwd.project.plannedBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }

                items(pwd.expenses) { expense ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(expense.purpose, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text(dateFormatter.format(Date(expense.date)), style = MaterialTheme.typography.bodySmall)
                        }
                        Text(currencyFormatter.format(expense.amount), style = MaterialTheme.typography.bodyMedium)
                    }
                    HorizontalDivider()
                }
                
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
fun MilestoneItem(milestone: Milestone, onToggle: () -> Unit, onCameraClick: () -> Unit) {
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val now = System.currentTimeMillis()
    val isOverdue = !milestone.isCompleted && milestone.expectedCompletionDate < now

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isOverdue) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = if (milestone.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Toggle Complete",
                    tint = if (milestone.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(milestone.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text("Expected: ${dateFormatter.format(Date(milestone.expectedCompletionDate))}", style = MaterialTheme.typography.bodySmall)
                if (milestone.isCompleted && milestone.actualCompletionDate != null) {
                    Text("Completed: ${dateFormatter.format(Date(milestone.actualCompletionDate))}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                } else if (isOverdue) {
                    Text("Overdue", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                }
                if (milestone.photoUri != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("📷 Photo Attached", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    if (milestone.locationCoordinates != null) {
                        Text("📍 ${milestone.locationCoordinates}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
            IconButton(onClick = onCameraClick) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "Take Photo",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

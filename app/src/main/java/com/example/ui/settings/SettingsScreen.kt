package com.example.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.GramVikasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: GramVikasViewModel,
    onBack: () -> Unit
) {
    val delayedAlertsEnabled by viewModel.delayedMilestoneAlertsEnabled.collectAsStateWithLifecycle()
    val budgetAlertsEnabled by viewModel.budgetOverrunAlertsEnabled.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            Text(
                text = "Notifications",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp, end = 16.dp)
            )
            
            ListItem(
                headlineContent = { Text("Delayed Milestone Alerts") },
                supportingContent = { Text("Receive warnings when a project milestone is past due") },
                trailingContent = {
                    Switch(
                        checked = delayedAlertsEnabled,
                        onCheckedChange = { viewModel.toggleDelayedMilestoneAlerts(it) }
                    )
                }
            )
            
            HorizontalDivider()
            
            ListItem(
                headlineContent = { Text("Budget Overrun Alerts") },
                supportingContent = { Text("Receive warnings when actual expenses exceed planned budget") },
                trailingContent = {
                    Switch(
                        checked = budgetAlertsEnabled,
                        onCheckedChange = { viewModel.toggleBudgetOverrunAlerts(it) }
                    )
                }
            )
        }
    }
}

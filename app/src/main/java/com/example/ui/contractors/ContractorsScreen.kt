package com.example.ui.contractors

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Contractor
import com.example.ui.GramVikasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractorsScreen(
    viewModel: GramVikasViewModel,
    onBack: () -> Unit
) {
    val contractors by viewModel.allContractors.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contractors") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(contractors) { contractor ->
                ContractorCard(contractor)
            }
        }
    }
}

@Composable
fun ContractorCard(contractor: Contractor) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(contractor.name, style = MaterialTheme.typography.titleMedium)
                Text("Active Projects: ${contractor.activeProjectsCount}", style = MaterialTheme.typography.bodyMedium)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(contractor.rating.toString(), style = MaterialTheme.typography.titleMedium)
                Icon(Icons.Default.Star, contentDescription = "Rating", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

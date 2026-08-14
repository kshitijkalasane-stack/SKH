package com.example.ui.login

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class Role(val title: String, val defaultPass: String, val icon: ImageVector) {
    ADMIN("Admin", "admin123", Icons.Default.AdminPanelSettings),
    PROJECT_HEAD("Project Head", "head123", Icons.Default.ManageAccounts),
    SITE_ENGINEER("Site Engineer", "site123", Icons.Default.Engineering)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (Role) -> Unit,
    onBack: () -> Unit
) {
    var selectedRole by remember { mutableStateOf(Role.ADMIN) }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Aesthetic Dark Colors
    val darkBackground = Color(0xFF0D1410)
    val surfaceDark = Color(0xFF1A2920)
    val accentGreen = Color(0xFF27B463)
    val textLight = Color(0xFFE8F5EE)
    val textMuted = Color(0xFF8B9E93)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") }, // Clean top bar
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = textLight)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = darkBackground,
                    navigationIconContentColor = textLight
                )
            )
        },
        containerColor = darkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            
            // Hero Section
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(surfaceDark)
                    .border(1.dp, accentGreen.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    tint = accentGreen,
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = textLight,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Select your portal access level",
                style = MaterialTheme.typography.bodyLarge,
                color = textMuted,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(40.dp))

            // Premium Role Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Role.entries.forEach { role ->
                    val isSelected = selectedRole == role
                    val bgColor by animateColorAsState(if (isSelected) accentGreen else surfaceDark, label = "bg")
                    val contentColor by animateColorAsState(if (isSelected) Color.White else textMuted, label = "content")
                    val borderColor by animateColorAsState(if (isSelected) accentGreen else Color.Transparent, label = "border")

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(bgColor)
                            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                            .clickable { selectedRole = role; errorMessage = null }
                            .padding(vertical = 16.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = role.icon,
                            contentDescription = role.title,
                            tint = contentColor,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = role.title.replace(" ", "\n"), // Stack words if possible
                            color = contentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            // Sleek Password Field
            OutlinedTextField(
                value = password,
                onValueChange = { 
                    password = it
                    errorMessage = null 
                },
                label = { Text("Access Key") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = "Toggle password visibility", tint = textMuted)
                    }
                },
                isError = errorMessage != null,
                supportingText = {
                    if (errorMessage != null) {
                        Text(text = errorMessage!!, color = Color(0xFFE57373))
                    } else {
                        Text("Default: ${selectedRole.defaultPass}", color = textMuted.copy(alpha = 0.7f))
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accentGreen,
                    unfocusedBorderColor = surfaceDark,
                    focusedLabelColor = accentGreen,
                    unfocusedLabelColor = textMuted,
                    focusedTextColor = textLight,
                    unfocusedTextColor = textLight,
                    cursorColor = accentGreen,
                    errorBorderColor = Color(0xFFE57373),
                    errorLabelColor = Color(0xFFE57373),
                    errorTextColor = textLight,
                    focusedContainerColor = surfaceDark.copy(alpha = 0.5f),
                    unfocusedContainerColor = surfaceDark.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Premium Login Button
            Button(
                onClick = {
                    if (password == selectedRole.defaultPass) {
                        onLoginSuccess(selectedRole)
                    } else {
                        errorMessage = "Incorrect access key for ${selectedRole.title}"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentGreen),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Authenticate", 
                    fontSize = 16.sp, 
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

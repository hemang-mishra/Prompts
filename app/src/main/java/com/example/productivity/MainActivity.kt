package com.example.productivity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.productivity.data.PromptDataStore
import com.example.productivity.ui.theme.ProductivityTheme
import kotlinx.coroutines.launch
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.productivity.viewmodel.PromptViewModel
import com.example.productivity.data.PromptEntity
import com.example.productivity.ui.PromptListScreen
import com.example.productivity.ui.PromptDetailScreen
import com.example.productivity.ui.BackupScreen
import com.example.productivity.ui.RemindersScreen
import com.example.productivity.viewmodel.CategoryViewModel
import com.example.productivity.ui.CategoryScreen
import com.example.productivity.R

@Composable
fun BiometricAuthScreen(
    context: Context,
    onAuthSuccess: () -> Unit
) {
    var authError by remember { mutableStateOf<String?>(null) }
    val activity = context as FragmentActivity
    val executor: Executor = ContextCompat.getMainExecutor(context)

    val promptInfo = remember {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Authenticate using your fingerprint")
            .setNegativeButtonText("Cancel")
            .build()
    }

    val biometricPrompt: BiometricPrompt = remember {
        BiometricPrompt(
            activity, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onAuthSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    authError = errString.toString()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    authError = "Authentication failed. Try again."
                }
            })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // App Icon/Logo Section
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_security_24),
                            contentDescription = "Security",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Title and Description
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Secure Access",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Authenticate to access your productivity tools",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                // Authentication Button
                Button(
                    onClick = {
                        biometricPrompt.authenticate(promptInfo)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_fingerprint_24),
                            contentDescription = "Fingerprint",
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Authenticate with Fingerprint",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Error Message
                if (authError != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_error_24),
                                contentDescription = "Error",
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = authError!!,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

class PromptViewModelFactory(private val application: android.app.Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PromptViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PromptViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val promptViewModel: PromptViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = PromptViewModelFactory(application)
            )
            val categoryViewModel: CategoryViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
                            @Suppress("UNCHECKED_CAST")
                            return CategoryViewModel(application) as T
                        }
                        throw IllegalArgumentException("Unknown ViewModel class")
                    }
                }
            )
            var isAuthenticated by remember { mutableStateOf(false) }
            var showPromptDetail by remember { mutableStateOf<PromptEntity?>(null) }
            val navController = rememberNavController()

            ProductivityTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = MaterialTheme.colorScheme.background,
                        bottomBar = {
                            if (isAuthenticated) {
                                BottomNavigationBar(navController = navController)
                            }
                        }
                    ) { innerPadding ->
                        if (!isAuthenticated) {
                            BiometricAuthScreen(
                                context = this@MainActivity,
                                onAuthSuccess = { isAuthenticated = true }
                            )
                        } else {
                            NavHost(
                                navController = navController,
                                startDestination = "prompts",
                                modifier = Modifier.padding(innerPadding)
                            ) {
                                composable("prompts") {
                                    PromptListScreen(
                                        viewModel = promptViewModel,
                                        onPromptClick = { prompt -> showPromptDetail = prompt },
                                        onAddPrompt = {
                                            promptViewModel.addPrompt(
                                                PromptEntity(
                                                    title = "New Prompt",
                                                    text = "",
                                                    frequency = 0
                                                )
                                            )
                                        },
                                        onPromptSend = { prompt ->
                                            promptViewModel.incrementFrequency(prompt.id)
                                            navigateToGPT(this@MainActivity, prompt.text)
                                        }
                                    )
                                    if (showPromptDetail != null) {
                                        PromptDetailScreen(
                                            prompt = showPromptDetail!!,
                                            onSave = { updatedPrompt ->
                                                promptViewModel.updatePrompt(updatedPrompt)
                                                showPromptDetail = null
                                            },
                                            onClose = { showPromptDetail = null },
                                            viewModel = promptViewModel
                                        )
                                    }
                                }
                                composable("reminders") {
                                    RemindersScreen(viewModel = promptViewModel)
                                }
                                composable("backup") {
                                    BackupScreen(viewModel = promptViewModel)
                                }
                                composable("categories") {
                                    CategoryScreen(viewModel = categoryViewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("Prompts", "prompts", R.drawable.outline_filter_list_24),
        BottomNavItem("Reminders", "reminders", R.drawable.baseline_access_time_filled_24),
        BottomNavItem("Backup", "backup", R.drawable.outline_settings_backup_restore_24),
        BottomNavItem("Categories", "categories", R.drawable.outline_category_24)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (currentRoute == item.route) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

data class BottomNavItem(
    val label: String,
    val route: String,
    val iconRes: Int
)

fun navigateToGPT(context: Context, prompt: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, prompt)
        type = "text/plain"
    }
    context.startActivity(intent)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ProductivityTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            // Preview content here
        }
    }
}
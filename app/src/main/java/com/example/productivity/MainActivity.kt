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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.productivity.viewmodel.CategoryViewModel
import com.example.productivity.ui.CategoryScreen
import androidx.compose.material.icons.filled.DateRange

@Composable
fun BiometricAuthScreen(
    context: Context, onAuthSuccess: () -> Unit
) {
    var authError by remember { mutableStateOf<String?>(null) }
    val activity = context as FragmentActivity
    val executor: Executor = ContextCompat.getMainExecutor(context)
    val promptInfo = remember {
        BiometricPrompt.PromptInfo.Builder().setTitle("Biometric Authentication")
            .setSubtitle("Authenticate using your fingerprint").setNegativeButtonText("Cancel")
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
    Column(
        modifier = Modifier
            .padding(32.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Authenticate to continue")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            biometricPrompt.authenticate(promptInfo)
        }) {
            Text("Authenticate with Fingerprint")
        }
        if (authError != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(authError!!, color = androidx.compose.ui.graphics.Color.Red)
        }
    }
}

class PromptViewModelFactory(private val application: android.app.Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PromptViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return PromptViewModel(application) as T
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
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (isAuthenticated) {
                            BottomNavigationBar(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    if (!isAuthenticated) {
                        BiometricAuthScreen(
                            context = this@MainActivity, onAuthSuccess = { isAuthenticated = true })
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
                                                title = "New Prompt", text = "", frequency = 0
                                            )
                                        )
                                    },
                                    onPromptSend = { prompt ->
                                        promptViewModel.incrementFrequency(prompt.id)
                                        navigateToGPT(this@MainActivity, prompt.text)
                                    })
                                if (showPromptDetail != null) {
                                    PromptDetailScreen(
                                        prompt = showPromptDetail!!,
                                        onSave = { updatedPrompt ->
                                            promptViewModel.updatePrompt(updatedPrompt)
                                            showPromptDetail = null
                                        },
                                        onClose = { showPromptDetail = null },
                                        viewModel = promptViewModel)
                                }
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

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("Prompts", "prompts", Icons.AutoMirrored.Filled.List),
        BottomNavItem("Backup", "backup", Icons.Default.Person),
        BottomNavItem("Categories", "categories", Icons.Default.DateRange)
    )
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                })
        }
    }
}

data class BottomNavItem(val label: String, val route: String, val icon: ImageVector)

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
    ProductivityTheme {}
}

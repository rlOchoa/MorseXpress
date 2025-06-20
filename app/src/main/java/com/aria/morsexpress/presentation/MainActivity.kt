package com.aria.morsexpress.presentation

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.aria.morsexpress.presentation.navigation.MorseXpressNavGraph
import com.aria.morsexpress.presentation.screen.imageinput.AnimatedOptionCard
import com.aria.morsexpress.presentation.theme.MorseXpressTheme
import kotlinx.coroutines.launch
import com.aria.morsexpress.R

class MainActivity : ComponentActivity() {

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val denied = permissions.filterValues { !it }.keys
        if (denied.isNotEmpty()) {
            Toast.makeText(this, "Permisos denegados: $denied", Toast.LENGTH_LONG).show()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestNecessaryPermissions()

        setContent {
            MorseXpressTheme {
                val navController = rememberNavController()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                val menuItems = listOf(
                    Triple("Inicio", "home", Icons.Default.Home),
                    Triple("Texto", "text_input", Icons.Default.Edit),
                    Triple("Imagen o Cámara", "camera_input_screen", Icons.Default.Camera),
                    Triple("Audio", "audio_input", Icons.Default.Mic),
                    Triple("Historial", "history", Icons.Default.History)
                )

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            AnimatedOptionCard(
                                iconRes = R.drawable.ic_morse,
                                title = "MorseXpress",
                                description = "Transforma texto, imágenes y audio a código Morse.",
                                onClick = { scope.launch { drawerState.close() } }
                            )
                            Divider()
                            menuItems.forEach { (label, route, icon) ->
                                NavigationDrawerItem(
                                    icon = { Icon(icon, contentDescription = null) },
                                    label = { Text(label) },
                                    selected = false,
                                    onClick = {
                                        navController.navigate(route)
                                        scope.launch { drawerState.close() }
                                    },
                                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                )
                            }
                        }
                    }
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text("MorseXpress") },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        scope.launch { drawerState.open() }
                                    }) {
                                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                                    }
                                }
                            )
                        }
                    ) { padding ->
                        Box(modifier = Modifier.padding(padding)) {
                            MorseXpressNavGraph(navController)
                        }
                    }
                }
            }
        }
    }

    private fun requestNecessaryPermissions() {
        val permissionsToRequest = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )

        if (Build.VERSION.SDK_INT in Build.VERSION_CODES.M..Build.VERSION_CODES.Q) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        permissionLauncher.launch(permissionsToRequest.toTypedArray())
    }
}
@Composable
fun QrCodeReaderApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Database
    val db = remember {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "qr-scanner-db"
        ).build()
    }

    val historyItems by db.scanHistoryDao().getAll().collectAsState(initial = emptyList())

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            if (navController.currentDestination?.route == "scanner") {
                ExtendedFloatingActionButton(
                    onClick = { /* Handle scan action */ },
                    icon = { Icon(Icons.Default.QrCodeScanner, "Scan") },
                    text = { Text("Scan QR Code") },
                    modifier = Modifier.padding(bottom = 56.dp)
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "scanner",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("scanner") {
                var lastResult by remember { mutableStateOf<String?>(null) }
                var showResultDialog by remember { mutableStateOf(false) }

                QrScannerScreen(
                    onScanResult = { result ->
                        lastResult = result
                        showResultDialog = true

                        // Save to history
                        scope.launch {
                            if (result.isValidUrl()) {
                                // Try to fetch preview
                                val preview = fetchUrlPreview(result)
                                db.scanHistoryDao().insert(
                                    ScanHistoryItem(
                                        content = result,
                                        previewTitle = preview?.title,
                                        previewImage = preview?.imageUrl
                                    )
                                )
                            } else {
                                db.scanHistoryDao().insert(
                                    ScanHistoryItem(content = result)
                                )
                            }
                        }
                    },
                    onError = { error ->
                        // Show error
                    }
                )

                if (showResultDialog && lastResult != null) {
                    ScanResultDialog(
                        result = lastResult!!,
                        onDismiss = { showResultDialog = false },
                        onOpenUrl = { url ->
                            context.openUrl(url)
                            showResultDialog = false
                        },
                        onCopy = { text ->
                            context.copyToClipboard(text)
                            // Show toast
                        },
                        onShare = { text ->
                            context.shareText(text)
                        },
                        onSave = { text ->
                            scope.launch {
                                db.scanHistoryDao().insert(
                                    ScanHistoryItem(content = text)
                                )
                            }
                            // Show toast
                        }
                    )
                }
            }

            composable("history") {
                HistoryScreen(
                    historyItems = historyItems,
                    onItemClick = { item ->
                        navController.navigate("details/${item.id}")
                    },
                    onDeleteItem = { item ->
                        scope.launch {
                            db.scanHistoryDao().delete(item)
                        }
                    },
                    onClearAll = {
                        scope.launch {
                            db.scanHistoryDao().clearAll()
                        }
                    }
                )
            }

            composable("generator") {
                QrGeneratorScreen(
                    onGenerate = { bitmap ->
                        // Save to gallery or database
                    }
                )
            }

            composable(
                route = "details/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType }
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getInt("id") ?: return@composable
                    val item = historyItems.firstOrNull { it.id == id } ?: return@composable

                    ScanDetailScreen(
                        item = item,
                        onBack = { navController.popBackStack() },
                        onDelete = {
                            scope.launch {
                                db.scanHistoryDao().delete(item)
                                navController.popBackStack()
                            }
                        }
                    )
                }
        }
    }
}

private val items = listOf(
    BottomNavItem(
        route = "scanner",
        title = "Scanner",
        icon = Icons.Default.QrCodeScanner
    ),
    BottomNavItem(
        route = "history",
        title = "History",
        icon = Icons.Default.History
    ),
    BottomNavItem(
        route = "generator",
        title = "Generator",
        icon = Icons.Default.QrCode
    )
)
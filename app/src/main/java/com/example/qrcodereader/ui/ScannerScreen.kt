@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QrScannerScreen(
    onScanResult: (String) -> Unit,
    onError: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val torchEnabled = remember { mutableStateOf(false) }

    val scannerOverlayState = remember { mutableStateOf(ScannerOverlayState()) }

    // Handle permission
    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (cameraPermissionState.status.isGranted) {
            val analyzer = remember {
                QrCodeAnalyzer { result ->
                    onScanResult(result)
                }
            }

            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val preview = androidx.camera.core.Preview.Builder().build()
                    val cameraSelector = CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()

                        // Set up torch if needed
                        val camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            analyzer
                        )

                        torchEnabled.value = false

                        camera.cameraControl.enableTorch(torchEnabled.value)
                    }, ContextCompat.getMainExecutor(ctx))

                    preview.setSurfaceProvider(previewView.surfaceProvider)
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )

            // Scanner overlay with animation
            ScannerOverlay(state = scannerOverlayState.value)

            // Torch toggle button
            IconButton(
                onClick = {
                    torchEnabled.value = !torchEnabled.value
                    // Need to update camera torch state here
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = if (torchEnabled.value) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                    contentDescription = "Toggle Torch",
                    tint = Color.White
                )
            }
        } else {
            PermissionDeniedContent(
                onRequestPermission = { cameraPermissionState.launchPermissionRequest() },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

class QrCodeAnalyzer(private val onScanResult: (String) -> Unit) : ImageAnalysis.Analyzer {
    private val scanner = BarcodeScanning.getClient()

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    barcodes.firstOrNull()?.rawValue?.let { result ->
                        onScanResult(result)
                    }
                }
                .addOnFailureListener {
                    // Handle error
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}
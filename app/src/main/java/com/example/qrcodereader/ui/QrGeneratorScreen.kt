@Composable
fun QrGeneratorScreen(
    onGenerate: (Bitmap) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }
    var size by remember { mutableStateOf(300) }
    var showDialog by remember { mutableStateOf(false) }
    var generatedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Enter text or URL") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                // Generate QR code when done typing
                if (text.isNotBlank()) {
                    generatedBitmap = generateQrCode(text, size)
                }
            })
        )

        Slider(
            value = size.toFloat(),
            onValueChange = { size = it.toInt() },
            valueRange = 200f..500f,
            steps = 3,
            modifier = Modifier.fillMaxWidth()
        )

        Text("QR Code Size: ${size}px")

        Button(
            onClick = {
                if (text.isNotBlank()) {
                    generatedBitmap = generateQrCode(text, size)
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Generate QR Code")
        }

        generatedBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Generated QR Code",
                modifier = Modifier
                    .size(size.dp)
                    .align(Alignment.CenterHorizontally)
                    .clickable { showDialog = true }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    onGenerate(bitmap)
                    showDialog = true
                }) {
                    Text("Save QR Code")
                }

                Button(onClick = {
                    shareBitmap(bitmap, "QR Code")
                }) {
                    Text("Share QR Code")
                }
            }
        }
    }

    if (showDialog && generatedBitmap != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("QR Code Generated") },
            text = {
                Image(
                    bitmap = generatedBitmap!!.asImageBitmap(),
                    contentDescription = "Generated QR Code",
                    modifier = Modifier.size(200.dp)
                )
            },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Done")
                }
            }
        )
    }
}

fun generateQrCode(content: String, size: Int): Bitmap {
    val hints = mapOf<EncodeHintType, Any>(
        EncodeHintType.MARGIN to 1,
        EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H
    )

    val matrix = QRCodeWriter().encode(
        content,
        BarcodeFormat.QR_CODE,
        size,
        size,
        hints
    )

    val width = matrix.width
    val height = matrix.height
    val pixels = IntArray(width * height)

    for (y in 0 until height) {
        for (x in 0 until width) {
            pixels[y * width + x] = if (matrix.get(x, y)) Color.BLACK else Color.WHITE
        }
    }

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    return bitmap
}
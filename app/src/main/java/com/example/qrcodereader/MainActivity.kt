class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            QrCodeReaderTheme {
                var locked by remember { mutableStateOf(true) }

                if (locked) {
                    AppLockScreen(
                        onUnlock = { locked = false }
                    )
                } else {
                    QrCodeReaderApp()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Check if we should lock the app when returning
    }
}
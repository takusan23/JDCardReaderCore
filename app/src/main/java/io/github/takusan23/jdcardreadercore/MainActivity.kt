package io.github.takusan23.jdcardreadercore

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.takusan23.jdcardreadercore.ui.screen.InfoScreen
import io.github.takusan23.jdcardreadercore.ui.screen.PinCodeScreen
import io.github.takusan23.jdcardreadercore.ui.theme.JDCardReaderCoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            JDCardReaderCoreTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colorScheme.background) {

                    val navController = rememberNavController()
                    val activity = LocalContext.current as Activity

                    NavHost(navController = navController, startDestination = "pincode") {
                        // 暗証番号入力画面
                        composable("pincode") {
                            PinCodeScreen { pin1, pin2 ->
                                navController.navigate("info?pin1=${pin1}&pin2=${pin2}")
                            }
                        }
                        // 記載事項表示
                        composable("info?pin1={pin1}&pin2={pin2}") {
                            val pin1 = it.arguments?.getString("pin1")
                            val pin2 = it.arguments?.getString("pin2")?.ifEmpty { null }

                            if (pin1 != null) {
                                InfoScreen(activity = activity, pin1 = pin1, pin2 = pin2)
                            }
                        }
                    }

                }
            }
        }

    }
}

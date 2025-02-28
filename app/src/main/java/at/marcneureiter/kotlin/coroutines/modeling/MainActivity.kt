package at.marcneureiter.kotlin.coroutines.modeling

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import at.marcneureiter.kotlin.coroutines.modeling.lib.permission.rememberPermissionManager
import at.marcneureiter.kotlin.coroutines.modeling.lib.runActivity.implementation.rememberAppSettingsRunner
import at.marcneureiter.kotlin.coroutines.modeling.lib.runActivity.implementation.rememberPickVisualMediaRunner
import at.marcneureiter.kotlin.coroutines.modeling.lib.runActivity.implementation.rememberRequestPermissionRunner
import at.marcneureiter.kotlin.coroutines.modeling.lib.runActivity.implementation.rememberTakePictureRunner
import at.marcneureiter.kotlin.coroutines.modeling.ui.theme.CoroutinesModelingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val viewModel: MainViewModel = viewModel()
            val state by viewModel.state.collectAsState()

            CoroutinesModelingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                    ) {
                        Text(state.message)

                        Button(onClick = viewModel::pickMedia) {
                            Text("Pick media")
                        }

                        Button(onClick = viewModel::takePicture) {
                            Text("Take picture")
                        }

                        state.picture?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = null
                            )
                        }
                    }
                }
            }

            val pickVisualMediaRunner = rememberPickVisualMediaRunner()
            val requestPermissionRunner = rememberRequestPermissionRunner()
            val takePictureRunner = rememberTakePictureRunner()
            val permissionManager = rememberPermissionManager()
            val appSettingsRunner = rememberAppSettingsRunner()

            LaunchedEffect(Unit) {
                viewModel.init(
                    pickVisualMediaRunner,
                    requestPermissionRunner,
                    takePictureRunner,
                    permissionManager,
                    appSettingsRunner
                )
            }
        }
    }
}
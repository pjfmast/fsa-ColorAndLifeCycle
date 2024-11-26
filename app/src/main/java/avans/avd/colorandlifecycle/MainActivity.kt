package avans.avd.colorandlifecycle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import avans.avd.colorandlifecycle.ui.theme.ColorAndLifeCycleTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ColorAndLifeCycleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                   ColoredBox(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ColoredBox(modifier: Modifier = Modifier) {
    // A surface container using the 'background' color from the theme
    val viewModel: ColorViewModel1 = viewModel()
    val flowColor by viewModel.color.collectAsStateWithLifecycle()


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(flowColor))
            .clickable { viewModel.generateNewColor() }
    )
}

// Option 1: survives configuration changes but not a 'killed by system' scenario
// Note that the ViewModel is independent od Compose libraries
class ColorViewModel1() : ViewModel() {
    private val _color = MutableStateFlow(0xFFFFFFFF)
    // public UI state which is exposed by the ColorViewModel
    val color = _color.asStateFlow()


    // public event handler which is exposed by the ColorViewModel
    fun generateNewColor() {
        val color = Random.nextLong(0xFFFFFFFF)
        _color.value = color
    }
}


// Option 2: survives configuration changes and 'killed by system'
// To reproduce in emulator surviving a 'kill by system' when app is in background:
// choose color, put app on background, in Logcat right-mouse click 'Kill proces' and put again in foreground
class ColorViewModel2(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    // public UI state which is exposed by the ColorViewModel
    val color = savedStateHandle.getStateFlow("color",0xFFFFFFFF)

    // public event handler which is exposed by the ColorViewModel
    fun generateNewColor() {
        val color = Random.nextLong(0xFFFFFFFF)
        savedStateHandle["color"] = color
    }
}
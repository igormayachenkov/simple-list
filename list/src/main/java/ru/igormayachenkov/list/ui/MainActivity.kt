package ru.igormayachenkov.list.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.runBlocking
import ru.igormayachenkov.list.App
import ru.igormayachenkov.list.Archivator
import ru.igormayachenkov.list.ui.theme.ListTheme

private const val TAG = "myapp.MainActivity"

class MainActivity : ComponentActivity() {

    companion object{
        var archivator : Archivator? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        archivator = Archivator(this)
        setContent {
            ListTheme {
                    val mainViewModel: MainViewModel = viewModel()

                    // Main screen
                    MainScreen(mainViewModel=mainViewModel)

                    // Settings screen
                    if(mainViewModel.isSettingsVisible)
                        SettingsScreen(onHide = mainViewModel::hideSettings)

                    // Data screen
                    if(mainViewModel.isDataVisible)
                        DataScreen(onHide = mainViewModel::hideData)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
        runBlocking {
            (application as App).listRepository.saveStack()
        }
    }

    // Doesn't work!
//    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
//        super.onSaveInstanceState(outState, outPersistentState)
//        Log.d(TAG, "onSaveInstanceState")
//    }
//    override fun onRestoreInstanceState(
//        savedInstanceState: Bundle?,
//        persistentState: PersistableBundle?
//    ) {
//        super.onRestoreInstanceState(savedInstanceState, persistentState)
//        Log.d(TAG, "onRestoreInstanceState")
//    }

}


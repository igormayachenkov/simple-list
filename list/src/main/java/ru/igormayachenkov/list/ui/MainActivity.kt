package ru.igormayachenkov.list.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.runBlocking
import ru.igormayachenkov.list.App
import ru.igormayachenkov.list.Database
import ru.igormayachenkov.list.ResultAPI
import ru.igormayachenkov.list.ui.theme.ListTheme

private const val TAG = "myapp.MainActivity"

class MainActivity : ComponentActivity() {

    companion object{
        var resultAPI : ResultAPI? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        resultAPI = ResultAPI(this)
        setContent {
            ListTheme {
                    val settingsViewModel: SettingsViewModel = viewModel()

                    // Main screen
                    MainScreen(settingsViewModel=settingsViewModel)

                    // Settings screen
                    SettingsScreen(settingsViewModel)

                    // Statistics screen
//                    if(mainViewModel.isDataVisible)
//                        DataScreen(onHide = mainViewModel::hideData)

                    // Data Info screen
                    InfoScreen()

                    // Saver screen
                    SaverScreen()

                    // Version upgraded screen
                    if(Database.isUpgraded)
                        VersionUpgraded()
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


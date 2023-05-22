package ru.igormayachenkov.list.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.runBlocking
import ru.igormayachenkov.list.App
import ru.igormayachenkov.list.Archivator
import ru.igormayachenkov.list.DataViewModel
import ru.igormayachenkov.list.ListViewModel
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
            var showSettings by rememberSaveable{ mutableStateOf<Boolean>(false) }

            ListTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val viewModel: ListViewModel = viewModel(factory = ListViewModel.Factory)
                    val dataViewModel:DataViewModel = viewModel()

                    BackHandler(
                        enabled = true,
                        onBack  = { if(!viewModel.onBackButtonClick()) this.finish() }
                    )
                    // Main Screen
                    MainScreen(viewModel,dataViewModel::show)

                    // Editor dialog
                    viewModel.editorData?.let {
                        Editor(
                            initialData = it,
                            onClose = viewModel::onEditorCancel,
                            onSave = viewModel::onEditorSave
                        )
                    }

                    // Settings dialog
                    if(viewModel.showSettingsEditor){
                        val settings   by viewModel.settings.collectAsState()
                        SettingsEditor(
                            settings,
                            onClose = viewModel::onSettingsEditorCancel,
                            onSave  = viewModel::onSettingsEditorSave
                        )
                    }

                    // Data screen
                    if(dataViewModel.isVisible)
                        DataScreen(dataViewModel)

                }
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


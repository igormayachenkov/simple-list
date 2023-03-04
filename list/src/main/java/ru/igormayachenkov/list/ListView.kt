package ru.igormayachenkov.list

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.igormayachenkov.list.data.*

private const val TAG = "myapp.ListView"

@Composable
fun ListView() {
    val viewModel:ListViewModel = viewModel()
    
    val openList = viewModel.openList
    val theItems:List<DataItem> = viewModel.openListItems
    var editingData:EditableData? by rememberSaveable() { mutableStateOf(null) }

    Log.d(TAG,"=>")

    val saveEditedData = fun(updatedData:EditableData):String?{
        Log.d(TAG,"onSave $updatedData")
        viewModel.updateOpenList(updatedData)?.let { return it }
        editingData=null
        return null
    }

    Column() {
        // HEADER
        Row(modifier= Modifier
            .fillMaxWidth()
            .padding(all = 3.dp)
            .background(Color.Blue)) {
            if(viewModel.backStack.isNotEmpty()) {
                Button(onClick = viewModel::onBackButtonClick) {
                    Text("back")
                }
            }
            Column() {
                Text(text = openList.name, style = MaterialTheme.typography.h5)
                openList.description?.let {
                    Text(text = it)
                }
            }
            
            Button(onClick = {editingData=openList.editableData}) {
                Text("E")
            }
        }
        
        // ITEMS LIST
        LazyColumn {
            items(theItems, { it.id }) { element ->
                Row(Modifier
                    .clickable(onClick = {viewModel.onListRowClick(element)})
                ) {
                    ItemRow(element)
                }
            }
        }
    }
    
    // EDITOR DIALOG
    editingData?.let{
        Editor(
            initialData = it,
            onClose={editingData=null},
            onSave=saveEditedData
        )
    }
}


@Composable
fun ItemRow(item:DataItem){
    Text(when(item.type){
        TYPE_ITEM->"item"
        TYPE_LIST->"list"
        else->item.type.toString()
    })
    Text(text = item.id.toString(), modifier=Modifier.padding(horizontal = 8.dp))
    Text(text = item.name,  style = MaterialTheme.typography.h5)
}


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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.igormayachenkov.list.data.DataItem
import ru.igormayachenkov.list.data.DataList
import ru.igormayachenkov.list.data.Element

private const val TAG = "myapp.ListView"

@Composable
fun ListView() {
    val viewModel:ListViewModel = viewModel()
    val theItems:List<Element> = viewModel.openListItems

    Log.d(TAG,"=>")

    Column() {
        Row(modifier= Modifier
            .fillMaxWidth()
            .padding(all = 3.dp)
            .background(Color.Blue)) {
            if(viewModel.backStack.isNotEmpty()) {
                Button(onClick = viewModel::onBackButtonClick) {
                    Text("back")
                }
            }
            Text(text = viewModel.openList.name, style = MaterialTheme.typography.h5)
        }
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
}

@Composable
fun ItemRow(element:Element){
    Text(when(element){
        is DataItem->"item"
        is DataList->"list"
    })
    Text(text = element.id.toString(), modifier=Modifier.padding(horizontal = 8.dp))
    when(element){
        is DataItem -> Text(text = element.name?:"null",  style = MaterialTheme.typography.h5)
        is DataList -> Text(text = element.name,  style = MaterialTheme.typography.h5)
    }

}


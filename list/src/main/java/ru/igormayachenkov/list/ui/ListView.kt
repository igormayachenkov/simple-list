package ru.igormayachenkov.list.ui

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.igormayachenkov.list.data.*

private const val TAG = "myapp.ListView"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListView(
    theItems:List<DataItem>,
    lazyListState : LazyListState,
    onItemClick:(DataItem)->Unit,
    onItemCheck:(DataItem)->Unit
){

    Log.d(TAG,"=> ") // DO NOT print lazyListState here! it causes rerendering

    LazyColumn(
        state = lazyListState,
        contentPadding = PaddingValues(vertical = 5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(items = theItems, key = { it.id }) { item ->
            ItemView(
                modifier = Modifier.animateItemPlacement(),
                item = item,
                onClick = onItemClick,
                onCheck = onItemCheck
                // IMPORTANT: USE STATIC CALLBACKS
                // onCheck = { viewModel.checkItem(item) } - CAUSES ALL LIST REDRAWING
            )
        }
    }
}

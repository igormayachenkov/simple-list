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
    settings:Settings,
    theItems:List<DataItem>,
    lazyListState : LazyListState,
    onItemClick:(DataItem)->Unit,
    onItemCheck:(DataItem)->Unit
){

    Log.d(TAG,"=> ") // DO NOT print lazyListState here! it causes rerendering

    val sortedItems = theItems.sortedWith(getDataItemComparator(settings))

    LazyColumn(
        state = lazyListState,
//        contentPadding = PaddingValues(vertical = 5.dp),
        contentPadding = PaddingValues(0.dp,5.dp,0.dp,if(settings.useFab)60.dp else 5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(items = sortedItems, key = { it.id }) { item ->
            Box(Modifier.animateItemPlacement()) {
                ItemView(
                    item = item,
                    onClick = onItemClick,
                    onCheck = onItemCheck,
                    settings = settings
                    // IMPORTANT: USE STATIC CALLBACKS
                    // onCheck = { viewModel.checkItem(item) } - CAUSES ALL LIST REDRAWING
                    // modifier = Modifier.animateItemPlacement() - THE SAME
                )
            }
        }
    }
}

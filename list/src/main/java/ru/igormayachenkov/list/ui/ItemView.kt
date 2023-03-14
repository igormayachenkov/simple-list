package ru.igormayachenkov.list.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import ru.igormayachenkov.list.R
import ru.igormayachenkov.list.data.*
import ru.igormayachenkov.list.ui.theme.ListTheme

private const val TAG = "myapp.ItemView"

@Composable
fun ItemView(
    modifier: Modifier,
    item:DataItem,
    onClick:(DataItem)->Unit,
    onCheck:(DataItem)->Unit
){
    Log.d(TAG,"=> #${item.id} ${item.name}")
    Card(
        modifier = modifier
        //    .background(color = MaterialTheme.colors.surface)
            .clickable(onClick = {onClick(item)})
    ) {
        if (item.type.hasChildren)
            ListRow(item = item)
        else
            ItemRow(item = item, onCheck = {onCheck(item)})
    }
}

@Composable
fun ListRow(item:DataItem){
    Row(
        Modifier
            .fillMaxWidth().padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Icon
        Icon(painter = painterResource(id = R.drawable.outline_featured_play_list_24),
            contentDescription = "list icon",
            tint = MaterialTheme.colors.onSurface)
        Spacer(modifier = Modifier.width(8.dp))
        // Name
        Text(text = item.name, style = MaterialTheme.typography.body1)
    }
}

@Composable
fun ItemRow(item:DataItem, onCheck:()->Unit){
    Row(
        Modifier
            .fillMaxWidth().padding(start = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            Modifier.fillMaxWidth().weight(1f).padding(vertical = 8.dp)
        ) {
            // Name
            Text(
                text = item.name, style = MaterialTheme.typography.body1,
            )

            // Description
            item.description?.let {
                Text(text = it, style = MaterialTheme.typography.body2)
            }
        }

        // Checkbox
        if (item.type.isCheckable) {
            IconButton(onClick = onCheck) {
                Icon(
                    painter = painterResource(id =
                    if (item.state.isChecked) R.drawable.baseline_check_box_24
                    else R.drawable.baseline_check_box_outline_blank_24
                    ),
                    contentDescription = "",
                    tint = MaterialTheme.colors.onSurface)
            }
        }
    }
}
//--------------------------------------------------------------------------------------------
// PREVIEW
fun fakeList():DataItem=DataItem(
    13,
    0,
    DataItem.Type(true, false),
    DataItem.State(false),
    "The List",
    "This is the list description text"
)
fun fakeItem():DataItem=DataItem(
    13,
    0,
    DataItem.Type(false, true),
    DataItem.State(true),
    "The Item",
    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque faucibus, sapien eget tristique commodo, magna nibh rutrum mauris, et tempor neque libero et lectus. Etiam vitae maximus diam, eu pulvinar nibh. Fusce sodales at nibh id accumsan."
)

@Preview(name = "ListRow")
@Composable
fun ListRowPreview(){
    Surface {
        ItemView(Modifier,item = fakeList(), onClick = {}, onCheck = {})
    }
}
@Preview(name = "ListRow Dark")
@Composable
fun ListRowDarkPreview(){
    ListTheme(darkTheme = true) {
        Surface {
            ItemView(Modifier,item = fakeList(), onClick = {}, onCheck = {})
        }
    }
}

@Preview(name = "ItemRow")
@Composable
fun ItemRowPreview(){
    Surface {
        ItemView(Modifier,item = fakeItem(), onClick = {}, onCheck = {})
    }
}
@Preview(name = "ItemRow Dark")
@Composable
fun ItemRowDarkPreview(){
    ListTheme(darkTheme = true) {
        Surface {
            ItemView(Modifier,item = fakeItem().copy(description = null), onClick = {}, onCheck = {})
        }
    }
}
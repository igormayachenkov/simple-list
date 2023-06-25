package ru.igormayachenkov.list.ui

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import ru.igormayachenkov.list.R
import ru.igormayachenkov.list.data.*
import ru.igormayachenkov.list.ui.theme.ListTheme
import ru.igormayachenkov.list.ui.theme.onSurfaceDisabled

private const val TAG = "myapp.ItemView"

@Composable
fun ItemView(
    item:DataItem,
    onOpenItem :(DataItem)->Unit,
    onCheckItem:(DataItem)->Unit,
    settings: Settings
){
    Log.d(TAG,"=> #${item.id} ${item.name}")
    if (item.type.hasChildren)
        ListRow(item, onOpenItem, onCheckItem, settings)
    else if (settings.useOldListUi)
        ItemRowV1(item, onOpenItem, onCheckItem, settings)
    else
        ItemRow(item, onOpenItem, onCheckItem, settings)
}

@Composable
fun Name(text:String, color:Color = Color.Unspecified){
    Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.body1,
    )
}
@Composable
fun Descr(text:String, color:Color){
    Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.body2,
    )
}

@Composable
fun getItemColor(isChecked:Boolean):Color =
    if(isChecked) MaterialTheme.colors.onSurfaceDisabled
        else      MaterialTheme.colors.onSurface //Color.Unspecified

@Composable
fun ListRow(
    item:DataItem,
    onOpenItem :(DataItem)->Unit,
    onCheckItem:(DataItem)->Unit,
    settings: Settings
){
    Card(
        modifier = Modifier
            .clickable(onClick = {onOpenItem(item)})
        //    .background(color = MaterialTheme.colors.surface)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Icon
            Icon(
                painter = painterResource(id = R.drawable.outline_featured_play_list_24),
                contentDescription = "list icon",
                tint = MaterialTheme.colors.onSurface
            )
            Spacer(modifier = Modifier.width(8.dp))
            // Name
            Name(text = item.name)
        }
    }
}

@Composable
fun ItemRow(
    item:DataItem,
    onOpenItem :(DataItem)->Unit,
    onCheckItem:(DataItem)->Unit,
    settings: Settings
){
    val color:Color = getItemColor(
        item.type.isCheckable && item.state.isChecked && settings.useCheckedColor)

    Card(
        modifier = Modifier
            .clickable(onClick = {onOpenItem(item)})
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp)
            ) {
                // Name
                Name(text = item.name, color = color)
                // Description
                item.description?.let {
                    Descr(text = it, color = color)
                }
            }

            // Checkbox
            if (item.type.isCheckable) {
                IconButton(onClick = { onCheckItem(item) }) {
                    Icon(
                        painter = painterResource(
                            id =
                            if (item.state.isChecked) R.drawable.baseline_check_box_24
                            else R.drawable.baseline_check_box_outline_blank_24
                        ),
                        contentDescription = "",
                        tint = color
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemRowV1(
    item:DataItem,
    onOpenItem :(DataItem)->Unit,
    onCheckItem:(DataItem)->Unit,
    settings: Settings
){
    val color:Color = getItemColor(item.state.isChecked)

    Card(
        modifier = Modifier
            .combinedClickable (
                onClick     = {onCheckItem(item)},
                onLongClick = {onOpenItem(item)}
            )
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp)
            ) {
                // Name
                Name(text = item.name, color = color)
                // Description
                item.description?.let {
                    Descr(text = it, color = color)
                }
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
    DataItem.State(false),
    "The Item",
    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque faucibus, sapien eget tristique commodo, magna nibh rutrum mauris, et tempor neque libero et lectus. Etiam vitae maximus diam, eu pulvinar nibh. Fusce sodales at nibh id accumsan."
)

@Preview(name = "ListRow")
@Composable
fun ListRowPreview(){
    Surface {
        ItemView(item = fakeList(), onOpenItem = {}, onCheckItem = {}, settings = Settings())
    }
}
@Preview(name = "ListRow Dark")
@Composable
fun ListRowDarkPreview(){
    ListTheme(darkTheme = true) {
        Surface {
            ItemView(item = fakeList(), onOpenItem = {}, onCheckItem = {}, settings = Settings())
        }
    }
}

@Preview(name = "ItemRow")
@Composable
fun ItemRowPreview(){
    ListTheme(darkTheme = false) {
        Surface {
            ItemView(item = fakeItem(), onOpenItem = {}, onCheckItem = {}, settings = Settings())
        }
    }
}
@Preview(name = "ItemRow Dark")
@Composable
fun ItemRowDarkPreview(){
    ListTheme(darkTheme = true) {
        Surface {
            ItemView(item = fakeItem().copy(description = null), onOpenItem = {}, onCheckItem = {}, settings = Settings())
        }
    }
}
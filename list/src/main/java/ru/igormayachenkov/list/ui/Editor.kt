package ru.igormayachenkov.list.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.igormayachenkov.list.data.DataItem
import ru.igormayachenkov.list.data.EditorData
import ru.igormayachenkov.list.ui.theme.ListTheme

@Composable
fun Editor(
    initialData:EditorData,
    onClose:()->Unit,
    onSave:(DataItem?)->String?
){
    val isNew:Boolean = initialData.isNew
    val initialItem:DataItem = initialData.item
    var name        by rememberSaveable { mutableStateOf<String>(initialItem.name) }
    var descr       by rememberSaveable { mutableStateOf<String>(initialItem.description?:"") }
    var hasChildren by rememberSaveable { mutableStateOf<Boolean>(initialItem.type.hasChildren) }
    var isCheckable by rememberSaveable { mutableStateOf<Boolean>(initialItem.type.isCheckable) }
    var error       by rememberSaveable { mutableStateOf<String?>(null) }
    var confirm     by rememberSaveable { mutableStateOf<String?>(null) }

    fun handleDelete(){
        onSave(null)?.let { error = it }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color(0x80000040)
    ){
        Card(
            Modifier
                .padding(top = 50.dp, start = 20.dp, end = 20.dp)
                .wrapContentHeight(align = Alignment.Top)
        ) {
            Column(
                Modifier
                    .padding(all = 16.dp)
                    .fillMaxWidth()
                //.padding(all = 16.dp)
            ) {
                // Header
                //Text((if(isNew)"New element" else "Edit existed"))

                // LIST / ITEM SWITCH
                if(isNew) Row(
                    Modifier.fillMaxWidth()
                ) {
                    val colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.surface,
                        disabledBackgroundColor = MaterialTheme.colors.primary,
                        disabledContentColor = MaterialTheme.colors.onPrimary,
                    )
                    Button(
                        modifier = Modifier.weight(1F),
                        colors = colors,
                        enabled = !hasChildren,
                        onClick = {hasChildren=!hasChildren}
                    ) {
                        Text("List")
                    }
                    Button(
                        modifier = Modifier.weight(1F),
                        colors = colors,
                        enabled = hasChildren,
                        onClick = {hasChildren=!hasChildren}) {
                        Text("Item")
                    }
                }

                // INPUTS
                // Name
                TextField(value = name,  onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {Text("<name>")},
                    textStyle = MaterialTheme.typography.body1
                )
                if(!hasChildren) { // Item
                    // Description
                    TextField(
                        value = descr, onValueChange = { descr = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("<description>") },
                        textStyle = MaterialTheme.typography.body2
                    )
                    // Is Checkable
                    Row(Modifier.fillMaxWidth().padding(top = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center) {
                        Text( text = "Is checkable")
                        Spacer( modifier = Modifier.width(5.dp))
                        Switch(checked = isCheckable, onCheckedChange = { isCheckable = it })
                    }
                }

                // BUTTONS
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)) {
                    // Delete
                    if(!isNew) {
                        Button(onClick = {
                            confirm = if(hasChildren)
                                "Delete the list and all it's items?"
                            else
                                "Delete the item?"
                        }) { Text(text = "Delete") }
                    }

                    // Cancel
//                    Button(onClick = onClose) {
//                        Text("Close")
//                    }

                    // Spacer
                    Spacer(modifier = Modifier.weight(1F))

                    // Save
                    Button(onClick = {
                        onSave(
                            initialItem.copy(
                                name=name,
                                description = descr.ifBlank { null },
                                type = initialItem.type.copy(
                                    hasChildren = hasChildren,
                                    isCheckable = isCheckable
                                )
                            )
                        )?.let { error=it }
                    }) {
                        Text(text = (if(isNew)"Insert" else "Save"))
                    }
                }
            }
//            Row(horizontalArrangement = Arrangement.End) {
//                IconButton(
//                    modifier = Modifier,
//                    onClick = onClose) {
//                    Icon(Icons.Default.Close, contentDescription = "")
//                }
//            }
        }
    }
    
    error?.let{
        AlertDialog(
            onDismissRequest = {error=null},
            title = { Text(text = "Error") },
            text = { Text(it) },
            buttons = {
                Button(onClick = {error=null}) {
                    Text("OK", fontSize = 22.sp) }
            }
        )
    }

    confirm?.let{
        AlertDialog(
            onDismissRequest = {error=null},
            title = { Text(text = "Confirmation") },
            text = { Text(it) },
            buttons = { Row {
                Button(onClick = { confirm = null }) {
                    Text("Cancel", fontSize = 22.sp) }
                Button(onClick = { confirm = null; handleDelete() }) {
                    Text("OK", fontSize = 22.sp) }
            }}
        )
    }
}

@Preview(showBackground = false)
@Composable
fun EditorPreview() {
    ListTheme(darkTheme = true) {
        Editor(EditorData(
            true,
            DataItem(13, 0, DataItem.Type(false, true), DataItem.State(true), "name", "descr")
        ),
            {},
            { null }
        )
    }
}
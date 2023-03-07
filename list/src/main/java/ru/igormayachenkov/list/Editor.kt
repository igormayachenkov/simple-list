package ru.igormayachenkov.list

import androidx.compose.foundation.background
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
        Box(
            Modifier
                //.background(Color.Red)
                .wrapContentSize(align = Alignment.Center)
        ) {
            Column(
                Modifier
                    .background(Color.DarkGray)
                    .padding(all = 16.dp)
                //.padding(all = 16.dp)
            ) {
                // Header
                Text((if(isNew)"New element" else "Edit existed"))
                // Inputs
                TextField(value = name,  onValueChange = { name = it })
                TextField(value = descr, onValueChange = { descr = it })
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Has children")
                    Switch(checked = hasChildren, onCheckedChange = { hasChildren = it })
                }
                // Buttons
                Row() {
                    // Delete
                    if(!isNew) {
                        Button(onClick = {
                            confirm = if(hasChildren)
                                "Delete the list and all it's items?"
                            else
                                "Delete the item?"
                        }) { Text(text = "Delete") }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    // Cancel
                    Button(onClick = onClose) {
                        Text("Close")
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    // Save
                    Button(onClick = {
                        onSave(
                            initialItem.copy(
                                name=name,
                                description = descr.ifBlank { null },
                                type = initialItem.type.copy(hasChildren=hasChildren)
                            )
                        )?.let { error=it }
                    }) {
                        Text(text = (if(isNew)"Insert" else "Save"))
                    }
                }
            }
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
    Editor(EditorData(true,
        DataItem(13,0, DataItem.Type(false,true), DataItem.State(true),"name","descr")),
        {},
        {null}
    )
}
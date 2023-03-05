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
import ru.igormayachenkov.list.data.EditableData

@Composable
fun Editor(
    initialData:EditableData,
    onClose:()->Unit,
    onSave:(EditableData)->String?
){
    var name    by rememberSaveable { mutableStateOf<String>(initialData.name) }
    var descr   by rememberSaveable { mutableStateOf<String>(initialData.descr) }
    var error   by rememberSaveable { mutableStateOf<String?>(null) }

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
                Text("Editor")
                TextField(value = name,  onValueChange = { name = it })
                TextField(value = descr, onValueChange = { descr = it })
                Row() {
                    Button(onClick = onClose) {
                        Text("Close")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = { 
                        onSave(EditableData(initialData.isNew, initialData.id,
                            name,descr))?.let { error=it }
                    }) {
                        Text("Save")
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
                Button(
                    onClick = {error=null}
                ) {
                    Text("OK", fontSize = 22.sp)
                }
            }
        )
    }
}

@Preview(showBackground = false)
@Composable
fun EditorPreview() {
    Editor(EditableData(false,13,"name","descr"), {},{null})
}
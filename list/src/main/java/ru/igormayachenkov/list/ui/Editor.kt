package ru.igormayachenkov.list.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.igormayachenkov.list.R
import ru.igormayachenkov.list.app
import ru.igormayachenkov.list.data.DataItem
import ru.igormayachenkov.list.data.EditorData
import ru.igormayachenkov.list.data.Settings
import ru.igormayachenkov.list.ui.theme.ListTheme

@Composable
fun Editor(
    initialData:EditorData,
    settings: Settings,
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

    val newItem = initialItem.copy(
        name = name.trim(),
        description = descr.trim().ifBlank { null },
        type = initialItem.type.copy(
            hasChildren = hasChildren,
            isCheckable = isCheckable
        )
    )

    fun onDelete(){
        onSave(null)?.let { error = it }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color(0x80000040)
    ){
        Card(
            Modifier
                .padding(top = 52.dp, start = 10.dp, end = 10.dp)
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
                    val shape = RoundedCornerShape(20.dp,20.dp,0.dp,0.dp)
                    Button(
                        modifier = Modifier.weight(1F),
                        colors = colors,
                        shape = shape,
                        enabled = hasChildren,
                        onClick = {hasChildren=!hasChildren}) {
                        Text(stringResource(R.string.editor_tab_item))
                    }
                    Button(
                        modifier = Modifier.weight(1F),
                        colors = colors,
                        shape = shape,
                        enabled = !hasChildren,
                        onClick = {hasChildren=!hasChildren} ) {
                        Text(stringResource(R.string.editor_tab_list))
                    }
                }

                // INPUTS
                // Name
                TextField(value = name,  onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {Text(stringResource(R.string.editor_hint_name))},
                    textStyle = MaterialTheme.typography.body1
                )
                if(!hasChildren) { // Item
                    // Description
                    TextField(
                        value = descr, onValueChange = { descr = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.editor_hint_description)) },
                        textStyle = MaterialTheme.typography.body2
                    )
                    // Is Checkable
                    if(!settings.useOldListUi) Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center) {
                        Text( stringResource(R.string.editor_checkbox_checkable),
                            style=MaterialTheme.typography.body2)
                        Spacer( modifier = Modifier.width(5.dp))
                        Switch(checked = isCheckable, onCheckedChange = { isCheckable = it })
                    }
                }

                // BUTTONS
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)) {

                    // Cancel
                    Button(onClick = onClose) {
                        Icon(Icons.Default.ArrowBack,"")
                    }

                    // Spacer
                    Spacer(modifier = Modifier.weight(1F))

                    // Delete
                    Button(
                        enabled = !isNew,
                        onClick = {
                            if(hasChildren) {
                                confirm = app.getString(R.string.editor_confirm_delete_list)
                            }else if(settings.confirmDelete){
                                confirm = app.getString(R.string.editor_confirm_delete_item)
                            }else{
                                onDelete()
                            }
                        }
                    ) {
                        Text(text = stringResource(R.string.editor_button_delete))
                    }

                    // Spacer
                    Spacer(modifier = Modifier.weight(1F))

                    // Save
                    Button(
                        enabled = isNew || (initialItem!=newItem),
                        onClick = { onSave(newItem)?.let { error=it } },
                    ) {
                        Text(text = (if(isNew) stringResource(R.string.editor_button_insert)
                                    else stringResource(R.string.editor_button_save)))
                    }
                }
            }
        }
    }
    
    error?.let{
        AlertDialog(
            onDismissRequest = {error=null},
            title = { Text(stringResource(R.string.common_title_error)) },
            text = { Text(it) },
            buttons = {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = { error = null }) {
                        Text(stringResource(R.string.common_button_ok))
                    }
                }
            }
        )
    }

    confirm?.let{
        AlertDialog(
            onDismissRequest = {error=null},
            title = { Text(it) },
            buttons = {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = { confirm = null }) {
                        Text(stringResource(R.string.common_button_cancel)) }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(onClick = { confirm = null; onDelete() }) {
                        Text(stringResource(R.string.common_button_ok)) }
                }
            }
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
            settings = Settings(),
            onClose = {},
            onSave = { null }
        )
    }
}
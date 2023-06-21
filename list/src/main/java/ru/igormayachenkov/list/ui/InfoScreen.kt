package ru.igormayachenkov.list.ui


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.igormayachenkov.list.InfoRepository
import ru.igormayachenkov.list.app
import ru.igormayachenkov.list.data.DataInfo
import ru.igormayachenkov.list.data.Version
import ru.igormayachenkov.list.ui.theme.ListTheme

@Composable
fun InfoScreen(){
    val vm:InfoViewModel = viewModel()
    when(val state = vm.state.collectAsState().value){
        InfoRepository.State.Ready -> return
        InfoRepository.State.Busy -> Busy(vm::onClose)
        is InfoRepository.State.Error -> Error(state.message, vm::onClose)
        is InfoRepository.State.Success -> Success(state.info,vm::onClose, vm::onSaveAll, vm::onDeleteAll, vm::onLoadAll)
    }
    BackHandler(enabled = true, onBack = vm::onClose)
}
@Composable
private fun Frame(onClose:()->Unit, content: @Composable ColumnScope.() -> Unit){
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onClose),
        color = Color(0x80000040)
    ){
        Card( modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top),
        ){
            Column(modifier = Modifier
                .padding(all=16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                Text("Your data status", style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
                Text(text = "app version: ${try{app.version}catch(_:Exception){Version()}}",
                    Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.subtitle2
                )
                Spacer(modifier = Modifier.height(20.dp))
                content()
            }
        }
    }
}
@Composable
private fun Busy(onClose:()->Unit){
    Frame(onClose){
        Text(text = "Fetching info...", modifier = Modifier.padding(vertical = 70.dp))
    }
}
@Composable
private fun Error(message:String, onClose:()->Unit){
    Frame(onClose){
        Text(
            text = message,
            color = MaterialTheme.colors.error
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onClose) {
            Text(text = "Close")
        }
    }
}
@Composable
private fun Success(info:DataInfo,onClose:()->Unit,onSaveAll:()->Unit,onDeleteAll:()->Unit,onLoadAll:()->Unit) {
    var confirmDelete by rememberSaveable { mutableStateOf<Boolean>(false) }
    Frame(onClose){
        val (nLists,nItems) = info
        Row(Modifier.fillMaxWidth()) {
            Text("number of lists:", Modifier.weight(1F), fontStyle = FontStyle.Italic )
            Text("$nLists")
        }
        Row(Modifier.fillMaxWidth()) {
            Text("number of items:", Modifier.weight(1F), fontStyle = FontStyle.Italic )
            Text("$nItems")
        }
        Divider(modifier = Modifier.padding(vertical = 5.dp),color=MaterialTheme.colors.onSurface)
        Row(Modifier.fillMaxWidth()) {
            Text("total:", Modifier.weight(1F), fontStyle = FontStyle.Italic )
            Text("${nLists+nItems}")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ACTION BUTTONS
        val modifier = Modifier.weight(1f)
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onSaveAll, enabled = !info.isEmpty, modifier = modifier) {
                Text("Save")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { confirmDelete = true },
                enabled = !info.isEmpty,
                modifier = modifier
            ) {
                Text("Empty")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onLoadAll, modifier = modifier) {
                Text("Restore")
            }
        }
    }

    if(confirmDelete) {
        AlertDialog(
            onDismissRequest = {confirmDelete=false},
            title = { Text("Erase all data?",color=MaterialTheme.colors.error) },
            buttons = { Row(Modifier.padding(16.dp)) {
                Button(onClick = { confirmDelete=false }) {
                    Text("Cancel") }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = { confirmDelete=false; onDeleteAll() }) {
                    Text("Process") }
            }}
        )

    }
}

@Preview(showBackground = false)
@Composable
private fun BusyPreview() {
    ListTheme(darkTheme = true) {
        Busy(onClose = {})
    }
}
@Preview(showBackground = false)
@Composable
private fun ErrorPreview() {
    ListTheme(darkTheme = true) {
        Error("Error message", onClose = {})
    }
}
@Preview(showBackground = false)
@Composable
private fun SuccessPreview() {
    ListTheme(darkTheme = true) {
        Success(DataInfo(13,120),{},{},{},{})
    }
}
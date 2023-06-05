package ru.igormayachenkov.list.ui


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.igormayachenkov.list.InfoRepository
import ru.igormayachenkov.list.data.DataInfo
import ru.igormayachenkov.list.ui.theme.ListTheme

@Composable
fun InfoScreen(){
    val vm:InfoViewModel = viewModel()
    when(val state = vm.state.collectAsState().value){
        InfoRepository.State.Ready -> return
        InfoRepository.State.Busy -> Busy()
        is InfoRepository.State.Error -> Error(state.message, vm::onClose)
        is InfoRepository.State.Success -> Success(state.info, vm::onSaveAll, vm::onDeleteAll, vm::onLoadAll)
    }
    BackHandler(enabled = true, onBack = vm::onClose)
}
@Composable
private fun Frame(content: @Composable ColumnScope.() -> Unit){
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color(0x80000040)
    ){
        Card( modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top),
        ){
            Column(
                Modifier.padding(all=16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                Text("Your data status", style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(20.dp))
                content()
            }
        }
    }
}
@Composable
private fun Busy(){
    Frame(){
        Text(text = "Fetching info...")
    }
}
@Composable
private fun Error(message:String, onClose:()->Unit){
    Frame(){
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
private fun Success(info:DataInfo,onSaveAll:()->Unit,onDeleteAll:()->Unit,onLoadAll:()->Unit) {
    var confirmDelete by rememberSaveable { mutableStateOf<Boolean>(false) }
    Frame(){
        val (nLists,nItems) = info
        Row(Modifier.fillMaxWidth()) {
            Text("number of elements:", Modifier.weight(1F), fontStyle = FontStyle.Italic )
            Text("${nLists+nItems}")
        }
        Row(Modifier.fillMaxWidth()) {
            Text(text = "From them:")
        }
        Row(Modifier.fillMaxWidth()) {
            Text("number of lists:", Modifier.weight(1F), fontStyle = FontStyle.Italic )
            Text("$nLists")
        }
        Row(Modifier.fillMaxWidth()) {
            Text("number of items:", Modifier.weight(1F), fontStyle = FontStyle.Italic )
            Text("$nItems")
        }
        // ACTIONS
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onSaveAll, enabled = !info.isEmpty) {
            Text("Save data in a file")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {confirmDelete=true}, enabled = !info.isEmpty) {
            Text("Erase data")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onLoadAll) {
            Text("Restore data from the file")
        }
    }

    if(confirmDelete) {
        AlertDialog(
            onDismissRequest = {confirmDelete=false},
            title = { Text(text = "Confirmation") },
            text = { Text("Delete all elements?") },
            buttons = { Row {
                Button(onClick = { confirmDelete=false }) {
                    Text("Cancel", fontSize = 22.sp) }
                Button(onClick = { confirmDelete=false; onDeleteAll() }) {
                    Text("Process", fontSize = 22.sp) }
            }}
        )

    }
}

@Preview(showBackground = false)
@Composable
private fun BusyPreview() {
    ListTheme(darkTheme = true) {
        Busy()
    }
}
@Preview(showBackground = false)
@Composable
private fun ErrorPreview() {
    ListTheme(darkTheme = true) {
        Error("Error message",{})
    }
}
@Preview(showBackground = false)
@Composable
private fun SuccessPreview() {
    ListTheme(darkTheme = true) {
        Success(DataInfo(13,120),{},{},{})
    }
}
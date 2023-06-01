package ru.igormayachenkov.list.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.igormayachenkov.list.App
import ru.igormayachenkov.list.SaverRepository
import ru.igormayachenkov.list.ui.theme.ListTheme

@Composable
fun SaverScreen() {
    when(val state = App.saverRepository.state.collectAsState().value){
        SaverRepository.State.Ready -> return
        is SaverRepository.State.Busy    -> Busy(message = state.message)
        is SaverRepository.State.Error   -> Error(message = state.message)
        is SaverRepository.State.Success -> Success(message = state.message)
    }
}
@Composable
private fun Body(content: @Composable ColumnScope.() -> Unit){
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color(0x80000040)
    ){
        Card( modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.CenterVertically),
        ){
            Column(
                Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = content
            )
        }
    }
}

@Composable
private fun Busy(message:String){
    Body(){
        Text(text = message)
    }
}
@Composable
private fun Error(message:String){
    Body(){
        Text(
            text = message,
            color = MaterialTheme.colors.error
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = App.saverRepository::reset) {
            Text(text = "Close")
        }
    }
}
@Composable
private fun Success(message:String){
    Body(){
        Text(text = message)
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = App.saverRepository::reset) {
            Text(text = "Close")
        }
    }
}

@Preview(showBackground = false)
@Composable
private fun BusyPreview() {
    ListTheme(darkTheme = true) {
        Busy("Busy message")
    }
}

@Preview(showBackground = false)
@Composable
private fun ErrorPreview() {
    ListTheme(darkTheme = true) {
        Error("Error message")
    }
}

@Preview(showBackground = false)
@Composable
private fun SuccessPreview() {
    ListTheme(darkTheme = true) {
        Success("Success message")
    }
}

package ru.igormayachenkov.list.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.igormayachenkov.list.ui.theme.ListTheme

@Composable
fun VersionUpgradedContent() {
    val modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp)
    val textStyle = MaterialTheme.typography.body1
    Text("Nested lists", modifier, style = textStyle)
    Text("More sorting parameters", modifier, style = textStyle)
    Text("More settings added", modifier, style = textStyle)
    Text("More clear data saving/restoring", modifier, style = textStyle)
    //Text("More convenient UI. If you don't think so you could restore the old one in settings :-)", modifier, style = textStyle)
    Text("More convenient UI", modifier=Modifier.fillMaxWidth(), style = textStyle)
    Text("You could restore the old UI in settings!",
        modifier, style = MaterialTheme.typography.body2, color = MaterialTheme.colors.error)
}

@Composable
fun VersionUpgraded(){
    var isVisible by rememberSaveable{ mutableStateOf<Boolean>(true) }
    if(!isVisible) return
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color(0x80000040)
    ){
        Card( modifier = Modifier
            .padding(all = 16.dp)
            .wrapContentHeight(align = Alignment.Top),
        ){
            Column(modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                Text("In the new version:")
                Spacer(modifier = Modifier.height(16.dp))
                VersionUpgradedContent()
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { isVisible=false }) {
                    Text(text = "Close")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VersionUpgradedPreview() {
    ListTheme(darkTheme = true) {
        VersionUpgraded()
    }
}
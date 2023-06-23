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
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.igormayachenkov.list.R
import ru.igormayachenkov.list.ui.theme.ListTheme

@Composable
fun VersionUpgradedContent() {
    stringArrayResource(id = R.array.news).forEach {
        Text(text = it,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
            style = MaterialTheme.typography.body1)
    }
    Text(text = stringResource(id = R.string.news_help),
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.body2,
        color = MaterialTheme.colors.error)
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
                Text(stringResource(id = R.string.news_title), style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(16.dp))
                VersionUpgradedContent()
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { isVisible=false }) {
                    Text(text = stringResource(id = R.string.common_button_close))
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
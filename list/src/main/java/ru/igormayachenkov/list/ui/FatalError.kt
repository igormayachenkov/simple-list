package ru.igormayachenkov.list.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.igormayachenkov.list.ui.theme.ListTheme

@Composable
fun FatalError(e:Exception) {

        Card( modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        ){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colors.primary)
                        .padding(6.dp)) {
                    Text(text="FATAL ERROR",
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.onPrimary
                    )
                }
                Text(text="${e.message}",
                    Modifier.padding(5.dp),
                    style = MaterialTheme.typography.body1)
                Spacer(modifier = Modifier.height(height = 20.dp) )
                Text(text=e.stackTraceToString(),
                    style = MaterialTheme.typography.subtitle2)
            }
        }
}

@Preview(showBackground = false)
@Composable
private fun FatalErrorPreview() {
    ListTheme(darkTheme = true) {
        FatalError(Exception("The exception message"))
    }
}